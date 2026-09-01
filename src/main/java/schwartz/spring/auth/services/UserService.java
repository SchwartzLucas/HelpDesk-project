package schwartz.spring.auth.services;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import schwartz.spring.auth.domain.user.User;

@Service
public class UserService {

    public User getAuthenticatedUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            return user;
        }

        return null;
    }

}
