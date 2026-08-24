package schwartz.spring.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import schwartz.spring.app.domain.ticket.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, String> {


}
