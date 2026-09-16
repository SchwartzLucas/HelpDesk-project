package schwartz.spring.auth.domain.user;

import java.util.UUID;

public record LoginResponse(
        String token,
        Long id,
        UUID publicId,
        String name,
        String login,
        UserRole role,
        Long teamId,
        Long clientId
) {
}
