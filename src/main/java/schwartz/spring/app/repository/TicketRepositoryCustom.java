package schwartz.spring.app.repository;

import schwartz.spring.app.domain.ticket.Ticket;
import schwartz.spring.app.domain.ticket.TicketListRequest;
import schwartz.spring.auth.domain.user.User;

import java.util.List;

public interface TicketRepositoryCustom {

    List<Ticket> listAllWithFilters(User user, TicketListRequest request);
}