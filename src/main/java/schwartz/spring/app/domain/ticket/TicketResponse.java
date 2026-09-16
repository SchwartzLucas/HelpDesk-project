package schwartz.spring.app.domain.ticket;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record TicketResponse(
        UUID id,
        String publicCode,
        String title,
        String description,
        Integer priority,
        Integer status,
        Integer category,
        Long clientId,
        String clientName,
        Long teamId,
        String teamName,
        Long responsableId,
        String responsableName,
        LocalDateTime slaExpiration,
        LocalDateTime deadline,
        Long totalSeconds,
        LocalDateTime createdDate,
        LocalDateTime updatedDate
) {
    public static TicketResponse from(
            Ticket t,
            Map<Long, String> clientNames,
            Map<Long, String> teamNames,
            Map<Long, String> userNames,
            Map<Long, Long> totalSeconds
    ) {
        return new TicketResponse(
                t.getPublicId(),
                t.getPublicCode(),
                t.getTitle(),
                t.getDescription(),
                t.getPriority(),
                t.getStatus(),
                t.getCategory(),
                t.getClientId(),
                lookup(clientNames, t.getClientId()),
                t.getTeamId(),
                lookup(teamNames, t.getTeamId()),
                t.getResponsibleId(),
                lookup(userNames, t.getResponsibleId()),
                t.getSlaExpiration(),
                t.getDeadline(),
                totalSeconds.getOrDefault(t.getId(), 0L),
                t.getCreatedDate(),
                t.getUpdatedDate()
        );
    }

    private static String lookup(Map<Long, String> map, Long key) {
        if (key == null) {
            return null;
        }
        String name = map.get(key);
        return name != null ? name : "";
    }
}