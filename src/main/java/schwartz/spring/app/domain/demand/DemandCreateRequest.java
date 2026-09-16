package schwartz.spring.app.domain.demand;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DemandCreateRequest(
        @NotBlank(message = "Demand Title cannot be empty")
        @Size(max = 250, message = "Demand Title must have at most 250 characters ")
        String title,
        String description
) {
}
