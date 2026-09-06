package schwartz.spring.app.domain.ticket;

import java.time.LocalDate;

public record TicketListRequest(
    LocalDate created_date,
    Integer status,
    Integer category,
    String title,
    Long client_id,
    Long responsible_id,
    Integer priority,
    String public_code
) {
}
