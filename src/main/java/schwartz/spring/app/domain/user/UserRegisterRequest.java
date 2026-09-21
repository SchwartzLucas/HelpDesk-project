package schwartz.spring.app.domain.user;

public record UserRegisterRequest(
        String login,
        String password,
        UserRole role
) {
}
