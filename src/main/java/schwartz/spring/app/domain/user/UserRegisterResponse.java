package schwartz.spring.app.domain.user;

public record UserRegisterResponse(
        String login
) {
    public static UserRegisterResponse from(User user) {
        return new UserRegisterResponse(
          user.getLogin()
        );
    }
}
