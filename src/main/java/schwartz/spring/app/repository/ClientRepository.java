package schwartz.spring.app.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import schwartz.spring.app.domain.client.Client;


public interface ClientRepository extends JpaRepository<Client, Long> {

    boolean existsByEmailIgnoreCase(String email);

    Client findByUserClientId(@NonNull Long id);
}
