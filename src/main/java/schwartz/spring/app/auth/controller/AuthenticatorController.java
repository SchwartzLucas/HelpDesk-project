package schwartz.spring.app.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import schwartz.spring.Utils.Utils;
import schwartz.spring.app.domain.user.*;
import schwartz.spring.app.auth.services.TokenService;
import schwartz.spring.app.repository.UserRepository;
import schwartz.spring.app.services.UserService;

import java.util.Objects;

@RestController
@RequestMapping("/auth")
public class AuthenticatorController {


    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserService userService;


    public AuthenticatorController(AuthenticationManager authenticationManager, UserRepository userRepository,
                                   TokenService tokenService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userService = userService;
    }


    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Validated AuthenticationRequest request) {

        var userNamePassword = new UsernamePasswordAuthenticationToken(request.login(), request.password());
        try {
            var auth = this.authenticationManager.authenticate(userNamePassword);
            var token = tokenService.generateToken((User) Objects.requireNonNull(auth.getPrincipal()));
            return ResponseEntity.status(HttpStatus.OK).body(new LoginResponse(token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponse> register(@RequestBody @Validated UserRegisterRequest request) {
        User user = userService.register(request);
        if(Utils.isEmpty(user)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserRegisterResponse.from(user));
    }
}
