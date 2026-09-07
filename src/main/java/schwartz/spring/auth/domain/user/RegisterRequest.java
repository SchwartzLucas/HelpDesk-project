package schwartz.spring.auth.domain.user;

public record RegisterRequest(
        String login, String password, UserRole role
) {
}
