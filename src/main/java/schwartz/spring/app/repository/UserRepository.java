package schwartz.spring.app.repository;


import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import schwartz.spring.app.domain.user.User;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {


    User findByLogin(String login);
    User findByPublicId(UUID publicId);
    List<User> findAll(Specification<User> spec);
}
