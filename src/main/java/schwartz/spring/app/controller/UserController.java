package schwartz.spring.app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import schwartz.spring.Utils.Utils;
import schwartz.spring.app.domain.user.*;
import schwartz.spring.app.services.UserService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/list")
    public ResponseEntity<List<UserListResponse>> list(@RequestBody(required = false) UserListRequest request) {
        List<User> user = userService.list(request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(UserListResponse.from(user));
    }

    @PostMapping("/update/{user}")
    public ResponseEntity<UserUpdateResponse> update(@PathVariable("user") UUID user_public_id, @RequestBody @Validated UserUpdateRequest request) {
       User user = userService.update(user_public_id, request);
       if(Utils.isEmpty(user)){
           return ResponseEntity.status(HttpStatus.NO_CONTENT)
                   .body(null);
       }
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(null);
    }
}
