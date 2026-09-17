package schwartz.spring.app.domain.demand;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record DemandUpdateRequest(
        @NotNull(message = "User ID cannot be null")
        UUID user,
        @Size(max = 250, message = "Demand Title must have at most 250 characters ")
        String title,
        String description,
        DemandStatus status
) {
}
