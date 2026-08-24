package schwartz.spring.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import schwartz.spring.app.domain.client.Client;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, String> {

    @Override
    List<Client> findAll();
    List<Client> findAllByName(String name);
    List<Client> findAllById(String id);
    void deleteById(String id);
}
