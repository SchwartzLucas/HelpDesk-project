package schwartz.spring.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import schwartz.spring.app.domain.demand.DemandStatusHistory;

import java.util.List;

public interface DemandStatusHistoryRepository
        extends JpaRepository<DemandStatusHistory, Long> {

    List<DemandStatusHistory> findByDemand_IdOrderByChangedAtAsc(
            Long demandId
    );
}
