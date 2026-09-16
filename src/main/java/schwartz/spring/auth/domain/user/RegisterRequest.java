package schwartz.spring.auth.domain.user;

public record RegisterRequest(
        String name,
        String login,
        String password,
        UserRole role,
        Long teamId,
        Long clientId
) {
}
