package schwartz.spring.app.services;

import org.springframework.stereotype.Service;
import schwartz.spring.app.domain.demand.Demand;
import schwartz.spring.app.domain.demand.DemandCreateRequest;

@Service
public class DemandService {



    public static Demand create(DemandCreateRequest request) {
        Demand demand = new Demand();
        return demand;
    }
}
