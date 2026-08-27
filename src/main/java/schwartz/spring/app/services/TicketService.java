package schwartz.spring.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import schwartz.spring.app.domain.ticket.Ticket;
import schwartz.spring.app.domain.ticket.TicketCreateRequest;
import schwartz.spring.app.infra.PublicIdGenerator;
import schwartz.spring.app.repository.TicketRepository;

@Service
public class TicketService {

    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    PublicIdGenerator publicIdGenerator;

    public Ticket create(TicketCreateRequest request) {
        String title = request.title();
        String description = request.description();
        String category = request.category();
        Integer priority = request.prority();

        Ticket ticket = new Ticket();
        ticket.setPublicId(publicIdGenerator.generate());
        ticket.setTitle(title);
        ticket.setCategory(category);
        ticket.setDescription(description);
        ticket.setStatus(1);

        ticketRepository.saveAndFlush(ticket);


        return ticket;
    }
}
