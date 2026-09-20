package schwartz.spring.app.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import schwartz.spring.app.domain.user.AuthenticationRequest;
import schwartz.spring.app.domain.user.LoginResponse;
import schwartz.spring.app.domain.user.RegisterRequest;
import schwartz.spring.app.domain.user.User;
import schwartz.spring.app.auth.services.TokenService;
import schwartz.spring.app.repository.UserRepository;

import java.util.Objects;

@RestController
@RequestMapping("/auth")
public class AuthenticatorController{

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Validated AuthenticationRequest request){

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
    public ResponseEntity register(@RequestBody @Validated RegisterRequest data){
        if(this.userRepository.findByLogin(data.login()) != null) {
            return ResponseEntity.badRequest().build();
        }
        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        User newUser = new User(data.login(), encryptedPassword, data.role());

        this.userRepository.save(newUser);

        return ResponseEntity.ok().build();
    }
}
