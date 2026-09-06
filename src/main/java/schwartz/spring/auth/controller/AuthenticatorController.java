package schwartz.spring.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import schwartz.spring.auth.domain.user.AuthenticationDTO;
import schwartz.spring.auth.domain.user.LoginResponseDTO;
import schwartz.spring.auth.domain.user.RegisterDTO;
import schwartz.spring.auth.domain.user.User;
import schwartz.spring.auth.services.TokenService;
import schwartz.spring.auth.repository.user.UserRepository;

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
    public ResponseEntity login(@RequestBody @Validated AuthenticationDTO data){
        var userNamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        try {
            var auth = this.authenticationManager.authenticate(userNamePassword);
            var token = tokenService.generateToken((User) Objects.requireNonNull(auth.getPrincipal()));
            return ResponseEntity.status(HttpStatus.OK).body(new LoginResponseDTO(token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Validated RegisterDTO data){
        if(this.userRepository.findByLogin(data.login()) != null) {
            return ResponseEntity.badRequest().build();
        }
        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        User newUser = new User(data.login(), encryptedPassword, data.role());

        this.userRepository.save(newUser);

        return ResponseEntity.ok().build();
    }
}
