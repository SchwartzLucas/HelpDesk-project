package schwartz.spring.app.domain.team;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TeamCreateRequest(
        @NotBlank(message = "Team name cannot be null or empty")
        String name,

        String description,

        @NotNull(message = "Team manager cannot be null")
        Long managerId
) {
}