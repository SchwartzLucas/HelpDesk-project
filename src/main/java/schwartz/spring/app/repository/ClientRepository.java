package schwartz.spring.app.repository;

import org.hibernate.internal.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import schwartz.spring.app.domain.client.Client;

import java.util.List;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, String> {

    void deleteByPublicId(UUID publicId);
    boolean existsByEmailIgnoreCase(String email);
    Optional<Client> findByPublicId(UUID publicId);
    Optional<Client> findByPublicCode(String publicCode);
    Client findById(Long id);
}
