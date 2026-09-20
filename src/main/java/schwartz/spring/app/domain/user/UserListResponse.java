package schwartz.spring.app.domain.user;

import java.util.List;

public record UserListResponse(

) {
    public static List<UserListResponse> from(List<User> user){
        return user.stream().map(u -> new UserListResponse(

        )).toList();
    }
}
