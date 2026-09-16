package schwartz.spring.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import schwartz.spring.app.domain.ticket.TicketTimeEntry;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TicketTimeEntryRepository extends JpaRepository<TicketTimeEntry, Long> {

    Optional<TicketTimeEntry> findFirstByUserIdAndEndTimeIsNullOrderByStartTimeDesc(Long userId);

    List<TicketTimeEntry> findByUserIdAndTicketIdOrderByStartTimeAsc(Long userId, Long ticketId);

    List<TicketTimeEntry> findByTicketIdOrderByStartTimeAsc(Long ticketId);

    List<TicketTimeEntry> findByTicketIdIn(Collection<Long> ticketIds);
}