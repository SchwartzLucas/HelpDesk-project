package schwartz.spring.app.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import schwartz.spring.Utils.Utils;
import schwartz.spring.app.domain.user.*;
import schwartz.spring.app.services.UserService;

@RestController
@RequestMapping("/auth")
public class AuthenticatorController {

    private final UserService userService;

    public AuthenticatorController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Validated AuthenticationRequest request) {
        String token = userService.login(request);
        if (Utils.isEmpty(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(null);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ".concat(token))
                .body(LoginResponse.from(token));
    }

    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponse> register(@RequestBody @Validated UserRegisterRequest request) {
        User user = userService.register(request);
        if (Utils.isEmpty(user)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(UserRegisterResponse.from(user));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserRegisterResponse.from(user));
    }
}
