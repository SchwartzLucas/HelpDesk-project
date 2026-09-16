package schwartz.spring.app.domain.ticket;

import java.time.LocalDateTime;
import java.util.UUID;

public record TicketUpdateRequest(
        UUID id,
        String title,
        String description,
        Integer priority,
        Integer status,
        Long teamId,
        Long responsableId,
        Integer category,
        LocalDateTime deadline
) {
}