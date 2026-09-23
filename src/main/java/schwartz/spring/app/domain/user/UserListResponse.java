package schwartz.spring.app.domain.user;

import java.util.List;
import java.util.UUID;

public record UserListResponse(UUID user_id, String user_name) {
    public static List<UserListResponse> from(List<User> user) {
        return user.stream().map(u -> new UserListResponse(
                        u.getPublicId(),
                        u.getLogin()
                )
        ).toList();
    }
}
