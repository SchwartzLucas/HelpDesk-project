package schwartz.spring.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import schwartz.spring.app.domain.team.Team;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, String> {

    @Override
    List<Team> findAll();
    List<Team> findAllByDescription(String description);
    List<Team> findAllByPublicId(Long publicId);
    void deleteByPublicId(Long publicId);
}


