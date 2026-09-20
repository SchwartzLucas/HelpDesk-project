package schwartz.spring.app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import schwartz.spring.app.domain.user.User;
import schwartz.spring.app.domain.user.UserListRequest;
import schwartz.spring.app.services.UserService;
import schwartz.spring.app.domain.user.UserListResponse;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<UserListResponse>> list(@RequestBody(required = false) UserListRequest request) {
        List<User> user = userService.list(request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(UserListResponse.from(user));
    }
}
