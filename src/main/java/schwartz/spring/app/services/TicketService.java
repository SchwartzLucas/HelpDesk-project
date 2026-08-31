package schwartz.spring.app.services;

import org.springframework.stereotype.Service;
import schwartz.spring.Exceptions.MissingAttributeException;
import schwartz.spring.Exceptions.TicketNotFoundException;
import schwartz.spring.Utils.Utils;
import schwartz.spring.app.domain.ticket.Ticket;
import schwartz.spring.app.domain.ticket.TicketCreateRequest;
import schwartz.spring.app.domain.ticket.TicketUpdateRequest;
import schwartz.spring.app.infra.PublicIdGenerator;
import schwartz.spring.app.repository.TicketRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TicketService {

    TicketRepository ticketRepository;
    PublicIdGenerator publicIdGenerator;

    public Ticket create(TicketCreateRequest request) {
        String title = request.title();
        String description = request.description();
        String category = request.category();
        Integer priority = request.prority() != null ? request.prority() : 0;

        Ticket ticket = new Ticket();
        ticket.setPublicId(publicIdGenerator.generate());
        ticket.setTitle(title);
        ticket.setCategory(category);
        ticket.setDescription(description);
        ticket.setPriority(priority);
        ticket.setStatus(1);

        ticketRepository.saveAndFlush(ticket);

        ticket.setPublicCode(String.format(
                        "FXG-%s-%06d", ticket.getCreatedDate().getYear(), ticket.getId()
                )
        );

        ticketRepository.save(ticket);
        return ticket;
    }

    public Ticket update(TicketUpdateRequest request) {
        UUID id = request.id();
        String title = request.title();
        String description = request.description();
        Integer priority = request.priority();
        Integer status = request.status();
        Long team_id = request.team_id();
        Long responsible_id = request.responsable_id();
        LocalDateTime now = LocalDateTime.now();
        if (Utils.isEmpty(request.id())) {
            throw new MissingAttributeException("public id");
        }
        if (Utils.isEmpty(ticketRepository.findyByPublicId(request.id()))) {
            throw new TicketNotFoundException(request.id());
        }

        Ticket ticket = new Ticket();
        ticket.setPublicId(request.id());
        if (!Utils.isEmpty(title)) {
            ticket.setTitle(title);
        }
        if (!Utils.isEmpty(description)) {
            ticket.setDescription(description);
        }
        if (!Utils.isEmpty(priority)) {
            ticket.setPriority(priority);
        }
        if (!Utils.isEmpty(status)) {
            ticket.setStatus(status);
        }
        if (!Utils.isEmpty(team_id)) {
            ticket.setTeamId(team_id);
        }
        if (!Utils.isEmpty(responsible_id)) {
            ticket.setResponsibleId(responsible_id);
        }
        ticket.setUpdatedDate(now);

        ticketRepository.updateWherePublicId(ticket);

        return ticket;
    }
}
