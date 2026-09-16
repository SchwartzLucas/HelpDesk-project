package schwartz.spring.auth.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import schwartz.spring.auth.domain.user.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByLogin(String login);

    List<User> findAllByTeamId(Long teamId);

    List<User> findAllByIsActive(Byte isActive);
}