package schwartz.spring.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import schwartz.spring.app.domain.ticket.Ticket;

import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, String> {

    Ticket findByPublicId(UUID publicId);
}
