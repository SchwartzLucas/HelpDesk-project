package schwartz.spring.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import schwartz.spring.auth.domain.user.User;
import schwartz.spring.auth.domain.user.UserResponse;
import schwartz.spring.auth.domain.user.UserRole;
import schwartz.spring.auth.repository.user.UserRepository;
import schwartz.spring.auth.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me() {
        User user = userService.getAuthenticatedUser();
        String teamName = user.getTeamId() != null
                ? userRepository.findById(user.getTeamId()).map(User::getName).orElse("")
                : null;
        return ResponseEntity.ok(UserResponse.from(user, teamName));
    }

    @GetMapping("/list")
    public ResponseEntity<List<UserResponse>> list() {
        User user = userService.getAuthenticatedUser();
        if (!UserRole.ADMIN_USER.equals(user.getRole()) && !UserRole.SUPPORT_USER.equals(user.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas administradores podem listar usuários");
        }
        List<UserResponse> users = userRepository.findAll().stream()
                .map(u -> {
                    String teamName = u.getTeamId() != null
                            ? userRepository.findById(u.getTeamId()).map(User::getName).orElse("")
                            : null;
                    return UserResponse.from(u, teamName);
                })
                .toList();
        return ResponseEntity.ok(users);
    }
}