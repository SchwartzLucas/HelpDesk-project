package schwartz.spring.app.domain.demand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record DemandCreateRequest(
        @NotBlank(message = "Demand Title cannot be empty")
        @Size(max = 250, message = "Demand Title must have at most 250 characters ")
        String title,
        String description,
        @Null
        UUID user_id
) {
}
