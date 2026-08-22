package schwartz.spring.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import schwartz.spring.auth.infra.security.TokenService;
import schwartz.spring.auth.repository.user.UserRepository;

import java.util.Objects;

@RestController
@RequestMapping("/auth")
public class AuthenticatorController{

    private static final Logger logger = LoggerFactory.getLogger(AuthenticatorController.class);

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Validated AuthenticationDTO data){
        logger.info("Authentication request received for user: {}", data.login());

        var userNamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        logger.info("Authentication token created: {}", userNamePassword);

        try {
            var auth = this.authenticationManager.authenticate(userNamePassword);
            logger.info("Authentication successful for user: {}", auth.getName());

            var token = tokenService.generateToken((User) Objects.requireNonNull(auth.getPrincipal()));
            logger.info("Token generated for user: {}", auth.getName());

            return ResponseEntity.ok(new LoginResponseDTO(token));
        } catch (Exception e) {
            logger.error("Authentication failed for user: {}", data.login(), e);
            return ResponseEntity.status(401).body("Invalid credentials");
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
