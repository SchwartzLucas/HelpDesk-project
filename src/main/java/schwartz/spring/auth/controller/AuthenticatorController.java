package schwartz.spring.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import schwartz.spring.app.infra.PublicIdGenerator;
import schwartz.spring.auth.domain.user.*;
import schwartz.spring.auth.services.TokenService;
import schwartz.spring.auth.repository.user.UserRepository;

import java.util.Objects;

@RestController
@RequestMapping("/auth")
public class AuthenticatorController {

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private PublicIdGenerator publicIdGenerator;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Validated AuthenticationRequest request) {
        var userNamePassword = new UsernamePasswordAuthenticationToken(request.login(), request.password());
        try {
            var auth = this.authenticationManager.authenticate(userNamePassword);
            User user = (User) Objects.requireNonNull(auth.getPrincipal());
            var token = tokenService.generateToken(user);
            return ResponseEntity.status(HttpStatus.OK).body(new LoginResponse(
                    token,
                    user.getId(),
                    user.getPublicId(),
                    user.getName(),
                    user.getLogin(),
                    user.getRole(),
                    user.getTeamId(),
                    user.getClientId()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Validated RegisterRequest data) {
        if (this.userRepository.findByLogin(data.login()) != null) {
            return ResponseEntity.badRequest().build();
        }
        String name = data.name() != null ? data.name() : data.login();
        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        UserRole role = data.role() != null ? data.role() : UserRole.COMMON_USER;
        User newUser = new User(name, data.login(), encryptedPassword, role);
        newUser.setPublicId(publicIdGenerator.generate());
        newUser.setClientId(data.clientId());
        newUser.setTeamId(data.teamId());
        this.userRepository.saveAndFlush(newUser);

        newUser.setPublicCode(String.format("USR-%08d", newUser.getId()));
        this.userRepository.save(newUser);

        return ResponseEntity.ok().build();
    }
}
