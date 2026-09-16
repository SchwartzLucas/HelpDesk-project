package schwartz.spring.app.domain.team;

import java.util.UUID;

public record TeamResponse(
        UUID id,
        Long internalId,
        String publicCode,
        String name,
        String description,
        Long managerId,
        String managerName,
        Long memberCount
) {
}