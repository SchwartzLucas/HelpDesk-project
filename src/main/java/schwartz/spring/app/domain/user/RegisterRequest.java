package schwartz.spring.app.domain.user;

public record RegisterRequest(
        String login, String password, UserRole role
) {
}
