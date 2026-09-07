package schwartz.spring.auth.domain.user;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequest(
        @NotBlank(message = "Login cannot be empty")
        String login,
        @NotBlank(message = "password cannot be empty")
        String password
) {
}
