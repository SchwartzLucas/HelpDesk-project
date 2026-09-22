package schwartz.spring.app.domain.user;

import schwartz.spring.Exceptions.GlobalExceptionHandler;
import schwartz.spring.Utils.Utils;

import java.util.Map;

public record UserRegisterResponse(
        String login
) {
    public static UserRegisterResponse from(User user) {
        if(Utils.isEmpty(user)){
            // TODO exception

        }
        return new UserRegisterResponse(
          user.getLogin()
        );
    }
}
