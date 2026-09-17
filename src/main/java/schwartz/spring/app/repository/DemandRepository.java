package schwartz.spring.app.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import schwartz.spring.app.domain.demand.Demand;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface DemandRepository extends JpaRepository<Demand, Long>, JpaSpecificationExecutor<Demand> {
    @Query("SELECT CASE WHEN EXISTS (SELECT 1 FROM Demand d WHERE d.userId = :publicId) THEN MAX(d.userDemandId) ELSE 0 END FROM Demand d WHERE d.userId = :publicId")
    Long findMaxUserDemandID(UUID publicId);

    Demand findByPublicId(UUID publicId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "UPDATE demand d set d.stopped_time = :now where d.public_id = UUID_TO_BIN(:public_id)",
            nativeQuery = true)
    void updateStoppedTime(@Param("now") Instant now, @Param("public_id") UUID public_id);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "UPDATE demand d set d.started_time = :now where d.public_id = UUID_TO_BIN(:public_id)",
            nativeQuery = true)
    void updateStartTime(@Param(":now") Instant now, @Param("public_id") UUID publicId);
}
