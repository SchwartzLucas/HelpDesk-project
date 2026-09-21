package schwartz.spring.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import schwartz.spring.app.domain.client.Client;

import java.util.UUID;


public interface ClientRepository extends JpaRepository<Client, Long> {

    boolean existsByEmailIgnoreCase(String email);
    Client findClientById(Long id);

    Client findClientByPublicId(UUID publicId);
}
