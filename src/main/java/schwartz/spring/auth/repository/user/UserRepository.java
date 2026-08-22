package schwartz.spring.auth.repository.user;


import org.springframework.data.jpa.repository.JpaRepository;
import schwartz.spring.auth.domain.user.User;

public interface UserRepository extends JpaRepository<User, String> {


    User findByLogin(String login);
}
