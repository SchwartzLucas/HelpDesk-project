package schwartz.spring.app.domain.client;

import java.util.UUID;

public record ClientResponse(
        UUID id,
        String publicCode,
        String name,
        String email
) {
    public static ClientResponse from(Client client) {
        return new ClientResponse(
                client.getPublicId(),
                client.getPublicCode(),
                client.getName(),
                client.getEmail()
        );
    }
}
