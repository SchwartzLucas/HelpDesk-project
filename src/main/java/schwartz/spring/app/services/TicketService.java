package schwartz.spring.app.services;

import org.springframework.stereotype.Service;
import schwartz.spring.app.domain.ticket.Ticket;
import schwartz.spring.app.domain.ticket.TicketCreateRequest;
import schwartz.spring.app.infra.PublicIdGenerator;
import schwartz.spring.app.repository.TicketRepository;

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

    public void update(TicketUpdateRequest request){

    }
}
