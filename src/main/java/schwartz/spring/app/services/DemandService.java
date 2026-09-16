package schwartz.spring.app.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import schwartz.spring.app.domain.demand.Demand;
import schwartz.spring.app.domain.demand.DemandCreateRequest;
import schwartz.spring.app.infra.PublicIdGenerator;
import schwartz.spring.app.repository.DemandRepository;
import schwartz.spring.auth.domain.user.User;
import schwartz.spring.auth.repository.user.UserRepository;

@Service
public class DemandService {

    private final PublicIdGenerator publicIdGenerator;
    private final DemandRepository demandRepository;
    private final UserRepository userRepository;

    public DemandService(PublicIdGenerator publicIdGenerator, DemandRepository demandRepository, UserRepository userRepository) {
        this.publicIdGenerator = publicIdGenerator;
        this.demandRepository = demandRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Demand create(DemandCreateRequest request) {
        User user = userRepository.findByPublicId(request.user_id());
        Long user_demand_id = demandRepository.findMaxUserDemandID(user.getPublicId());
        Demand demand = new Demand();
        demand.setPublicId(publicIdGenerator.generate());
        demand.setTitle(request.title());
        demand.setDescription(request.description());
        demand.setUserId(user.getPublicId());
        demand.setUserDemandId(user_demand_id + 1);

        demandRepository.saveAndFlush(demand);

        demand.setPublicCode(String.format(
                        "DEM-%08d", demand.getUserDemandId()
                )
        );

        demandRepository.save(demand);

        return demand;
    }
}
