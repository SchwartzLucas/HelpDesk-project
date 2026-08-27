package schwartz.spring.app.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import schwartz.spring.Exceptions.ClientAlreadyExistsException;
import schwartz.spring.app.domain.client.Client;
import schwartz.spring.app.domain.client.ClientCreateRequest;
import schwartz.spring.app.infra.PublicIdGenerator;
import schwartz.spring.app.repository.ClientRepository;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final PublicIdGenerator publicIdGenerator;

    public ClientService(
            ClientRepository clientRepository,
            PublicIdGenerator publicIdGenerator
    ) {
        this.clientRepository = clientRepository;
        this.publicIdGenerator = publicIdGenerator;
    }

    @Transactional
    public Client create(ClientCreateRequest request) {
        String name = request.name();
        String email = request.email().trim().toLowerCase();

        if (clientRepository.existsByEmailIgnoreCase(email)) {
            throw new ClientAlreadyExistsException(email);
        }

        Client client = new Client();
        client.setPublicId(publicIdGenerator.generate());
        client.setName(name);
        client.setEmail(email);

        clientRepository.saveAndFlush(client);

        client.setPublicCode(String.format(
                "CLI-%08d", client.getId()
        ));

        clientRepository.save(client);

        return client;
    }
}
