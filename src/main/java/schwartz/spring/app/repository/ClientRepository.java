package schwartz.spring.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import schwartz.spring.app.domain.client.Client;


public interface ClientRepository extends JpaRepository<Client, Long> {

    boolean existsByEmailIgnoreCase(String email);
    Client findClientById(Long id);
}
