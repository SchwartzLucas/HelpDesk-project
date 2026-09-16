package schwartz.spring.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import schwartz.spring.app.domain.demand.Demand;

import java.util.UUID;

public interface DemandRepository extends JpaRepository<Demand, Long> {
    @Query("SELECT CASE WHEN EXISTS (SELECT 1 FROM Demand d WHERE d.userId = :publicId) THEN MAX(d.userDemandId) ELSE 0 END FROM Demand d WHERE d.userId = :publicId")
    Long findMaxUserDemandID(UUID publicId);
}
