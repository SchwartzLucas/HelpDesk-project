package schwartz.spring.app.domain.ticket;

import java.time.LocalDate;

public record TicketListRequest(
        String public_code,
        String title,
        Integer status,
        Integer category,
        Integer priority,
        Long client_id,
        Long team_id,
        Long responsable_id,
        LocalDate deadline_from,
        LocalDate deadline_to,
        String period,
        LocalDate created_date
) {
}