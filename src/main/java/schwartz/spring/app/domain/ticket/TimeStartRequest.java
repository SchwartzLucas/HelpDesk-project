package schwartz.spring.app.domain.ticket;

import java.util.UUID;

public record TimeStartRequest(
        UUID ticketId
) {
}