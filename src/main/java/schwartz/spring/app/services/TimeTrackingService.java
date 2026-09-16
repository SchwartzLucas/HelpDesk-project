package schwartz.spring.app.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import schwartz.spring.app.domain.ticket.Ticket;
import schwartz.spring.app.domain.ticket.TicketTimeEntry;
import schwartz.spring.app.domain.ticket.TimeEntryResponse;
import schwartz.spring.app.infra.PublicIdGenerator;
import schwartz.spring.app.repository.TicketRepository;
import schwartz.spring.app.repository.TicketTimeEntryRepository;
import schwartz.spring.auth.domain.user.User;
import schwartz.spring.auth.domain.user.UserRole;
import schwartz.spring.auth.repository.user.UserRepository;
import schwartz.spring.auth.services.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TimeTrackingService {

    private final TicketTimeEntryRepository timeEntryRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final PublicIdGenerator publicIdGenerator;
    private final UserService userService;

    public TimeTrackingService(
            TicketTimeEntryRepository timeEntryRepository,
            TicketRepository ticketRepository,
            UserRepository userRepository,
            PublicIdGenerator publicIdGenerator,
            UserService userService
    ) {
        this.timeEntryRepository = timeEntryRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.publicIdGenerator = publicIdGenerator;
        this.userService = userService;
    }

    @Transactional
    public TimeEntryResponse start(UUID ticketPublicId) {
        User user = userService.getAuthenticatedUser();
        Ticket ticket = requireAccessibleTicket(user, ticketPublicId);

        closeRunning(user.getId());

        TicketTimeEntry entry = new TicketTimeEntry();
        entry.setPublicId(publicIdGenerator.generate());
        entry.setTicketId(ticket.getId());
        entry.setUserId(user.getId());
        entry.setStartTime(LocalDateTime.now());
        entry.setCreatedDate(LocalDateTime.now());
        entry.setUpdatedDate(LocalDateTime.now());
        timeEntryRepository.save(entry);

        return toResponse(entry, ticket, user);
    }

    @Transactional
    public TimeEntryResponse pause(UUID ticketPublicId) {
        User user = userService.getAuthenticatedUser();
        Ticket ticket = requireAccessibleTicket(user, ticketPublicId);
        return closeTicketEntry(user, ticket);
    }

    @Transactional
    public TimeEntryResponse resume(UUID ticketPublicId) {
        return start(ticketPublicId);
    }

    @Transactional
    public TimeEntryResponse stop(UUID ticketPublicId) {
        User user = userService.getAuthenticatedUser();
        Ticket ticket = requireAccessibleTicket(user, ticketPublicId);
        return closeTicketEntry(user, ticket);
    }

    @Transactional(readOnly = true)
    public TimeEntryResponse getActive() {
        User user = userService.getAuthenticatedUser();
        return timeEntryRepository
                .findFirstByUserIdAndEndTimeIsNullOrderByStartTimeDesc(user.getId())
                .map(e -> toResponse(e, null, user))
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<TimeEntryResponse> listByTicket(UUID ticketPublicId) {
        User user = userService.getAuthenticatedUser();
        Ticket ticket = requireAccessibleTicket(user, ticketPublicId);
        return timeEntryRepository.findByTicketIdOrderByStartTimeAsc(ticket.getId())
                .stream()
                .map(e -> toResponse(e, ticket, user))
                .toList();
    }

    @Transactional(readOnly = true)
    public Long totalSeconds(UUID ticketPublicId) {
        Ticket ticket = ticketRepository.findByPublicId(ticketPublicId);
        if (ticket == null) {
            return 0L;
        }
        return timeEntryRepository.findByTicketIdOrderByStartTimeAsc(ticket.getId())
                .stream()
                .filter(e -> e.getEndTime() != null)
                .map(e -> java.time.Duration.between(e.getStartTime(), e.getEndTime()).getSeconds())
                .reduce(0L, Long::sum);
    }

    // ------------------------------------------------------------

    private void closeRunning(Long userId) {
        timeEntryRepository.findFirstByUserIdAndEndTimeIsNullOrderByStartTimeDesc(userId)
                .ifPresent(e -> {
                    e.setEndTime(LocalDateTime.now());
                    e.setUpdatedDate(LocalDateTime.now());
                    timeEntryRepository.save(e);
                });
    }

    private TimeEntryResponse closeTicketEntry(User user, Ticket ticket) {
        runningEntryOf(user.getId(), ticket.getId())
                .ifPresent(e -> {
                    e.setEndTime(LocalDateTime.now());
                    e.setUpdatedDate(LocalDateTime.now());
                    timeEntryRepository.save(e);
                });
        return toResponse(lastEntryOf(user.getId(), ticket.getId()), ticket, user);
    }

    private java.util.Optional<TicketTimeEntry> runningEntryOf(Long userId, Long ticketId) {
        return timeEntryRepository.findFirstByUserIdAndEndTimeIsNullOrderByStartTimeDesc(userId)
                .filter(e -> e.getTicketId().equals(ticketId));
    }

    private TicketTimeEntry lastEntryOf(Long userId, Long ticketId) {
        var list = timeEntryRepository.findByUserIdAndTicketIdOrderByStartTimeAsc(userId, ticketId);
        return list.isEmpty() ? null : list.get(list.size() - 1);
    }

    private Ticket requireAccessibleTicket(User user, UUID ticketPublicId) {
        Ticket ticket = ticketRepository.findByPublicId(ticketPublicId);
        if (ticket == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Demanda não encontrada");
        }
        boolean isAdmin = UserRole.ADMIN_USER.equals(user.getRole()) || UserRole.SUPPORT_USER.equals(user.getRole());
        if (!isAdmin && !user.getId().equals(ticket.getResponsibleId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você só pode cronometrar suas próprias demandas");
        }
        return ticket;
    }

    private TimeEntryResponse toResponse(TicketTimeEntry e, Ticket ticket, User user) {
        if (e == null) {
            return null;
        }
        if (ticket == null) {
            ticket = ticketRepository.findById(e.getTicketId()).orElse(null);
        }
        String userName = user != null ? user.getName() : "";
        if (user == null) {
            var u = userRepository.findById(e.getUserId());
            userName = u.map(User::getName).orElse("");
        }
        return TimeEntryResponse.from(e, ticket, userName);
    }
}