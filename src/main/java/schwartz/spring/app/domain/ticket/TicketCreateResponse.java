package schwartz.spring.app.domain.ticket;

import java.time.LocalDateTime;
import java.util.UUID;

public record TicketCreateResponse(
        UUID id,
        String title,
        LocalDateTime data_prevista_termino,
        Integer categoria
) {

    public static TicketCreateResponse from(Ticket ticket) {
        return new TicketCreateResponse(
                ticket.getPublicId(),
                ticket.getTitle(),
                ticket.getSlaExpiration(),
                ticket.getCategory()
        );
    }
}
