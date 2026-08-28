package schwartz.spring.Utils;

import org.springframework.security.core.context.SecurityContextHolder;
import schwartz.spring.auth.domain.user.User;

public class SecurityUtils {
    public static User getLoggedUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User)) {
            return null;
        }
        return (User) auth.getPrincipal();
    }
}
