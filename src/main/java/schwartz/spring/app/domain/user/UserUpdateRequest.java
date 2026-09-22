package schwartz.spring.app.domain.user;

import java.util.UUID;

public record UserUpdateRequest(
        String password,
        String login,
        Integer isActive,
        UserRole role,
        UUID client_id,
        UUID team_id
) {
}
