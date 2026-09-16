package schwartz.spring.auth.domain.user;

import java.util.UUID;

public record UserResponse(
        UUID id,
        Long internalId,
        String publicCode,
        String name,
        String login,
        UserRole role,
        Long teamId,
        String teamName,
        Long clientId
) {
    public static UserResponse from(User user, String teamName) {
        return new UserResponse(
                user.getPublicId(),
                user.getId(),
                user.getPublicCode(),
                user.getName(),
                user.getLogin(),
                user.getRole(),
                user.getTeamId(),
                teamName,
                user.getClientId()
        );
    }
}