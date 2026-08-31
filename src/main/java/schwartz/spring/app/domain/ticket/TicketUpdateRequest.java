package schwartz.spring.app.domain.ticket;

import java.util.UUID;

public record TicketUpdateRequest(
        UUID id,
        String title,
        String description,
        Integer priority,
        Integer status,
        Long team_id,
        Long responsable_id
) {
}
