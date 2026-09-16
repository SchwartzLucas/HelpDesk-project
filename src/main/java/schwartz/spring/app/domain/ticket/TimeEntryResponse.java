package schwartz.spring.app.domain.ticket;

import java.time.LocalDateTime;
import java.util.UUID;

public record TimeEntryResponse(
        UUID id,
        Long ticketId,
        UUID ticketPublicId,
        String ticketTitle,
        String ticketPublicCode,
        Long userId,
        String userName,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        Long durationSeconds,
        String status
) {
    public static TimeEntryResponse from(TicketTimeEntry e, Ticket ticket, String userName) {
        return new TimeEntryResponse(
                e.getPublicId(),
                ticket.getId(),
                ticket.getPublicId(),
                ticket.getTitle(),
                ticket.getPublicCode(),
                e.getUserId(),
                userName,
                e.getStartTime(),
                e.getEndTime(),
                e.getEndTime() != null
                        ? java.time.Duration.between(e.getStartTime(), e.getEndTime()).getSeconds()
                        : java.time.Duration.between(e.getStartTime(), java.time.LocalDateTime.now()).getSeconds(),
                e.getEndTime() == null ? "RUNNING" : "STOPPED"
        );
    }
}