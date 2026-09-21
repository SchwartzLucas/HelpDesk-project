package schwartz.spring.app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import schwartz.spring.app.domain.user.*;
import schwartz.spring.app.services.UserService;

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

    @PostMapping("/update")
    public ResponseEntity<UserUpdateResponse> update(@RequestBody(required = false) UserUpdateRequest request) {
        userService.update();
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(null);
    }
}
