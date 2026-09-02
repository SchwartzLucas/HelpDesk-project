package schwartz.spring.app.domain.ticket;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TicketCreateRequest(
        @NotBlank(message = "Ticket title cannot be null or empty")
        @Size(max = 250, message = "Ticket title must have at most 250 characters")
        String title,

        @NotBlank(message = "Ticket category cannot be null or empty")
        Integer category,

        @NotBlank(message = "Ticket description cannot be null or empty")
        @Size(max = 5000, message = "Ticket description must have at most 5000 characters")
        String description,
        Integer prority

) {
}
