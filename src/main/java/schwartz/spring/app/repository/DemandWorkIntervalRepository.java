package schwartz.spring.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import schwartz.spring.app.domain.demand.DemandWorkInterval;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DemandWorkIntervalRepository extends JpaRepository<DemandWorkInterval, Long> {

    List<DemandWorkInterval> findByDemand_IdOrderByStartedAtAsc(Long demandId);

    List<DemandWorkInterval> findByDemand_PublicIdOrderByStartedAtAsc(UUID publicId);

    Optional<DemandWorkInterval> findFirstByDemand_IdAndEndedAtIsNull(Long demandId);

    boolean existsByDemand_IdAndEndedAtIsNull(Long demandId);
}