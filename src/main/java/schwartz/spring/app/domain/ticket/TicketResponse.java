package schwartz.spring.app.domain.ticket;

import java.time.LocalDateTime;
import java.util.UUID;

public record TicketResponse(
        UUID id,
        String title,
        LocalDateTime data_prevista_termino,
        String categoria
) {

    public static TicketResponse from(Ticket ticket) {
        return new TicketResponse(
                ticket.getPublicId(),
                ticket.getTitle(),
                ticket.getSlaExpiration(),
                ticket.getCategory()
        );
    }
}
