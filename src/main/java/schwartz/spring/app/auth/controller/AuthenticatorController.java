package schwartz.spring.app.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import schwartz.spring.Exceptions.LoginException;
import schwartz.spring.Utils.Utils;
import schwartz.spring.app.domain.user.*;
import schwartz.spring.app.services.UserService;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthenticatorController {

    private final UserService userService;

    public AuthenticatorController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request) {
        try {
            String token = userService.login(request);
            return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, "Bearer " + token).body(LoginResponse.from(token));

        } catch (LoginException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Erro interno no servidor"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponse> register(@RequestBody @Validated UserRegisterRequest request) {
        User user = userService.register(request);
        if (Utils.isEmpty(user)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(UserRegisterResponse.from(user));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(UserRegisterResponse.from(user));
    }
}
