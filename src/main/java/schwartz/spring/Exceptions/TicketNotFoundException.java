package schwartz.spring.Exceptions;

import java.util.UUID;

public class TicketNotFoundException extends RuntimeException {
    public TicketNotFoundException(UUID id) {
        super("The ticket not exists");
    }
}
