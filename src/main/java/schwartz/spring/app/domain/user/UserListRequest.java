package schwartz.spring.app.domain.user;

import schwartz.spring.Utils.Filter;

public record UserListRequest(
        String login,
        Integer active,
        Integer role,
        String public_code,
        Filter filter
) {
}
