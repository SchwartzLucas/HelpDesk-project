package schwartz.spring.app.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;
import schwartz.spring.Exceptions.MissingAttributeException;
import schwartz.spring.Exceptions.TicketNotFoundException;
import schwartz.spring.Utils.Utils;
import schwartz.spring.app.domain.client.Client;
import schwartz.spring.app.domain.team.Team;
import schwartz.spring.app.domain.ticket.*;
import schwartz.spring.app.infra.PublicIdGenerator;
import schwartz.spring.app.repository.ClientRepository;
import schwartz.spring.app.repository.TeamRepository;
import schwartz.spring.app.repository.TicketRepository;
import schwartz.spring.app.repository.TicketTimeEntryRepository;
import schwartz.spring.auth.domain.user.User;
import schwartz.spring.auth.domain.user.UserRole;
import schwartz.spring.auth.repository.user.UserRepository;
import schwartz.spring.auth.services.UserService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final PublicIdGenerator publicIdGenerator;
    private final UserService userService;
    private final ClientRepository clientRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TicketTimeEntryRepository timeEntryRepository;

    @PersistenceContext
    private EntityManager em;

    public TicketService(
            TicketRepository ticketRepository,
            PublicIdGenerator publicIdGenerator,
            UserService userService,
            ClientRepository clientRepository,
            TeamRepository teamRepository,
            UserRepository userRepository,
            TicketTimeEntryRepository timeEntryRepository,
            EntityManager em
    ) {
        this.ticketRepository = ticketRepository;
        this.publicIdGenerator = publicIdGenerator;
        this.userService = userService;
        this.clientRepository = clientRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.timeEntryRepository = timeEntryRepository;
        this.em = em;
    }

    public List<TicketResponse> create(TicketCreateRequest request) {
        User user = userService.getAuthenticatedUser();
        boolean isAdmin = UserRole.ADMIN_USER.equals(user.getRole()) || UserRole.SUPPORT_USER.equals(user.getRole());

        Long clientId = request.clientId();
        if (clientId == null) {
            clientId = user.getClientId();
        } else if (!isAdmin) {
            clientId = user.getClientId();
        }

        Client client = clientId != null ? clientRepository.findClientById(clientId) : null;
        if (client == null) {
            throw new schwartz.spring.Exceptions.ClientNotFoundException("Cliente não encontrado");
        }

        Ticket ticket = new Ticket();
        ticket.setPublicId(publicIdGenerator.generate());
        ticket.setTitle(request.title());
        ticket.setCategory(request.category() != null ? request.category() : 1);
        ticket.setDescription(request.description());
        ticket.setPriority(request.priority() != null ? request.priority() : 1);
        ticket.setStatus(1);
        ticket.setClientId(client.getId());
        ticket.setTeamId(request.teamId());
        ticket.setResponsibleId(request.responsableId());
        ticket.setDeadline(request.deadline());
        ticket.setCreatedDate(LocalDateTime.now());
        ticket.setUpdatedDate(LocalDateTime.now());
        ticket.setSlaExpiration(request.deadline() != null ? request.deadline() : LocalDateTime.now().plusDays(3L));

        ticketRepository.saveAndFlush(ticket);

        ticket.setPublicCode(String.format(
                "FXG-%s-%06d", ticket.getCreatedDate().getYear(), ticket.getId()
        ));

        ticketRepository.save(ticket);

        return toResponse(List.of(ticket));
    }

    public List<TicketResponse> update(TicketUpdateRequest request) {
        UUID id = request.id();

        if (Utils.isEmpty(id)) {
            throw new MissingAttributeException("public id");
        }

        Ticket existing = ticketRepository.findByPublicId(id);
        if (Utils.isEmpty(existing)) {
            throw new TicketNotFoundException();
        }

        StringBuilder sql = new StringBuilder("UPDATE ticket SET ");
        List<String> sets = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();

        if (!Utils.isEmpty(request.title())) {
            sets.add("title = :title");
            params.put("title", request.title());
        }
        if (!Utils.isEmpty(request.description())) {
            sets.add("description = :description");
            params.put("description", request.description());
        }
        if (request.priority() != null) {
            sets.add("priority = :priority");
            params.put("priority", request.priority());
        }
        if (request.status() != null) {
            sets.add("status = :status");
            params.put("status", request.status());
        }
        if (request.category() != null) {
            sets.add("category = :category");
            params.put("category", request.category());
        }
        if (request.teamId() != null) {
            sets.add("team_id = :team_id");
            params.put("team_id", request.teamId());
        }
        if (request.responsableId() != null) {
            sets.add("responsable_id = :responsable_id");
            params.put("responsable_id", request.responsableId());
        }
        if (request.deadline() != null) {
            sets.add("deadline = :deadline");
            params.put("deadline", request.deadline());
        }

        if (sets.isEmpty()) {
            return toResponse(List.of(existing));
        }

        sets.add("updated_date = :updated_date");
        params.put("updated_date", now);

        sql.append(String.join(", ", sets));
        sql.append(" WHERE public_id = :public_id");
        params.put("public_id", id);

        var query = em.createNativeQuery(sql.toString());
        params.forEach(query::setParameter);
        query.executeUpdate();

        return toResponse(List.of(ticketRepository.findByPublicId(id)));
    }

    public List<TicketResponse> list(TicketListRequest request) {
        User user = userService.getAuthenticatedUser();
        boolean isAdmin = UserRole.ADMIN_USER.equals(user.getRole()) || UserRole.SUPPORT_USER.equals(user.getRole());

        StringBuilder sql = new StringBuilder("SELECT t.* FROM ticket t WHERE 1=1");
        List<String> conditions = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        // usuários comuns veem apenas as suas demandas
        if (!isAdmin) {
            conditions.add("t.responsable_id = :uid");
            params.put("uid", user.getId());
        }

        if (request != null) {
            if (Utils.isEmpty(request.period())) {
                if (request.created_date() != null) {
                    conditions.add("t.created_date >= :c_from AND t.created_date < :c_to");
                    params.put("c_from", request.created_date().atStartOfDay());
                    params.put("c_to", request.created_date().plusDays(1).atStartOfDay());
                }
                if (request.deadline_from() != null) {
                    conditions.add("t.deadline >= :d_from");
                    params.put("d_from", request.deadline_from().atStartOfDay());
                }
                if (request.deadline_to() != null) {
                    conditions.add("t.deadline < :d_to");
                    params.put("d_to", request.deadline_to().plusDays(1).atStartOfDay());
                }
            } else {
                addPeriodCondition(request.period(), conditions, params);
            }

            if (request.status() != null) {
                conditions.add("t.status = :status");
                params.put("status", request.status());
            }
            if (request.category() != null) {
                conditions.add("t.category = :category");
                params.put("category", request.category());
            }
            if (request.priority() != null) {
                conditions.add("t.priority = :priority");
                params.put("priority", request.priority());
            }
            if (request.client_id() != null && isAdmin) {
                conditions.add("t.client_id = :client_id");
                params.put("client_id", request.client_id());
            }
            if (request.team_id() != null) {
                conditions.add("t.team_id = :team_id");
                params.put("team_id", request.team_id());
            }
            if (request.responsable_id() != null && isAdmin) {
                conditions.add("t.responsable_id = :responsable_id");
                params.put("responsable_id", request.responsable_id());
            }
            if (!Utils.isEmpty(request.title())) {
                conditions.add("LOWER(t.title) LIKE :title");
                params.put("title", "%" + request.title().trim().toLowerCase() + "%");
            }
            if (!Utils.isEmpty(request.public_code())) {
                conditions.add("LOWER(t.public_code) LIKE :public_code");
                params.put("public_code", "%" + request.public_code().trim().toLowerCase() + "%");
            }
        }

        if (!conditions.isEmpty()) {
            sql.append(" AND ").append(String.join(" AND ", conditions));
        }
        sql.append(" ORDER BY (t.deadline IS NULL) ASC, t.deadline ASC, t.created_date DESC");

        Query query = em.createNativeQuery(sql.toString(), Ticket.class);
        params.forEach(query::setParameter);
        List<Ticket> tickets = query.getResultList();

        return toResponse(tickets);
    }

    private void addPeriodCondition(String period, List<String> conditions, Map<String, Object> params) {
        LocalDate today = LocalDate.now();
        switch (period.toLowerCase()) {
            case "today" -> {
                conditions.add("t.deadline >= :p_from AND t.deadline < :p_to");
                params.put("p_from", today.atStartOfDay());
                params.put("p_to", today.plusDays(1).atStartOfDay());
            }
            case "week" -> {
                conditions.add("t.deadline >= :p_from AND t.deadline < :p_to");
                params.put("p_from", today.atStartOfDay());
                params.put("p_to", today.plusDays(7).atStartOfDay());
            }
            case "month" -> {
                LocalDate nextMonth = today.withDayOfMonth(1).plusMonths(1);
                conditions.add("t.deadline >= :p_from AND t.deadline < :p_to");
                params.put("p_from", today.atStartOfDay());
                params.put("p_to", nextMonth.atStartOfDay());
            }
            case "overdue" -> {
                conditions.add("t.deadline < :p_from");
                conditions.add("t.status NOT IN (4, 5)");
                params.put("p_from", today.atStartOfDay());
            }
            case "all" -> {
            }
            default -> {
            }
        }
    }

    // ------------------------------------------------------------
    // Enriquecimento: nomes de cliente/time/pessoa + tempo total
    // ------------------------------------------------------------
    private List<TicketResponse> toResponse(List<Ticket> tickets) {
        if (Utils.isEmpty(tickets)) {
            return Collections.emptyList();
        }

        Set<Long> clientIds = tickets.stream().map(Ticket::getClientId)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> teamIds = tickets.stream().map(Ticket::getTeamId)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> userIds = tickets.stream().map(Ticket::getResponsibleId)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        userIds.addAll(tickets.stream().map(Ticket::getResponsibleId)
                .filter(Objects::nonNull).collect(Collectors.toSet()));

        Map<Long, String> clientNames = clientRepository.findAllById(clientIds).stream()
                .collect(Collectors.toMap(Client::getId, Client::getName, (a, b) -> a));
        Map<Long, String> teamNames = teamRepository.findAllById(teamIds).stream()
                .collect(Collectors.toMap(Team::getId, Team::getName, (a, b) -> a));
        Map<Long, String> userNames = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getName, (a, b) -> a));

        Set<Long> ticketIds = tickets.stream().map(Ticket::getId).collect(Collectors.toSet());
        Map<Long, Long> totalSeconds = timeEntryRepository.findByTicketIdIn(ticketIds).stream()
                .filter(e -> e.getEndTime() != null)
                .collect(Collectors.groupingBy(
                        TicketTimeEntry::getTicketId,
                        Collectors.summingLong(e -> Duration.between(e.getStartTime(), e.getEndTime()).getSeconds())
                ));

        return tickets.stream()
                .map(t -> TicketResponse.from(t, clientNames, teamNames, userNames, totalSeconds))
                .toList();
    }
}