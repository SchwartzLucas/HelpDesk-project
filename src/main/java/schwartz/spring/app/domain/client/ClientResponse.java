package schwartz.spring.app.domain.client;

import java.util.UUID;

public record ClientResponse(
        UUID id,
        Long internalId,
        String publicCode,
        String name,
        String email
) {
    public static ClientResponse from(Client client) {
        return new ClientResponse(
                client.getPublicId(),
                client.getId(),
                client.getPublicCode(),
                client.getName(),
                client.getEmail()
        );
    }
}