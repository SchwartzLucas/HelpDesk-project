package schwartz.spring.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import schwartz.spring.app.domain.demand.Demand;

public interface DemandRepository extends JpaRepository<Demand, Long> {
}
