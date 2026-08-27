package schwartz.spring.app.domain.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClientCreateRequest(

        @NotBlank(message = "Client name cannot be null or empty")
        @Size(max = 250, message = "Client name must have at most 250 characters")
        String name,

        @NotBlank(message = "Client e-mail cannot be null or empty")
        @Email(message = "Email informed is invalid")
        @Size(max = 250, message = "Client e-mail must have at most 250 characters")
        String email
) {
}
