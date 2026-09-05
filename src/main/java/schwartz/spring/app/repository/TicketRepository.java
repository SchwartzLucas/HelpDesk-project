package schwartz.spring.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import schwartz.spring.app.domain.ticket.Ticket;

import java.util.List;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, String> {

    Ticket findByPublicId(UUID publicId);

    @Query("select t from ticket t where t.clientId = :id")
    List<Ticket> listAllByClient_id(@Param("id") Long id);
}
