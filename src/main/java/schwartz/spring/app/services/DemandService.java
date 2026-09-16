package schwartz.spring.app.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import schwartz.spring.app.domain.demand.Demand;
import schwartz.spring.app.domain.demand.DemandCreateRequest;
import schwartz.spring.app.domain.demand.DemandUpdateRequest;
import schwartz.spring.app.infra.PublicIdGenerator;
import schwartz.spring.app.repository.DemandRepository;
import schwartz.spring.auth.domain.user.User;
import schwartz.spring.auth.repository.user.UserRepository;

import java.time.Instant;
import java.util.UUID;

@Service
public class DemandService {
    private final static Integer DEMAND_STATUS_CREATED = 0;
    private final static Integer DEMAND_STATUS_ACTIVE = 1;
    private final static Integer DEMAND_STATUS_STOPPED = 2;
    private final static Integer DEMAND_STATUS_FINISHED = 3;
    private final static Integer DEMAND_STATUS_CANCELED = 4;
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
        demand.setDemandStatus(DEMAND_STATUS_CREATED);
        demandRepository.saveAndFlush(demand);
        demand.setPublicCode(String.format(
                        "DEM-%08d", demand.getUserDemandId()
                )
        );
        demandRepository.save(demand);
        return demand;
    }

    public Demand stop(UUID demandPublicId) {
        // 0 = created demand | 1 = actual active demand | 2 = stopped demand | 3 = demand finished | 4 = canceled demand
        Demand demand = demandRepository.findByPublicId(demandPublicId);
        if (!DEMAND_STATUS_ACTIVE.equals(demand.getDemandStatus())) {
            return null;
        }
        Instant now = Instant.now();
        demandRepository.updateStoppedTime(now, demand.getPublicId());
        demand.setStoppedTime(now);
        demand.setDemandStatus(DEMAND_STATUS_STOPPED);
        return demand;
    }

    // TODO ver como fazer exceptions
    public Demand start(UUID demandPublicId) {
        Demand demand = demandRepository.findByPublicId(demandPublicId);
        if (!DEMAND_STATUS_ACTIVE.equals(demand.getDemandStatus())
                || DEMAND_STATUS_FINISHED.equals(demand.getDemandStatus())) {
            return null;
        }
        Instant now = Instant.now();
        demandRepository.updateStartTime(now, demand.getPublicId());
        demand.setStartedTime(now);
        demand.setDemandStatus(DEMAND_STATUS_ACTIVE);
        return demand;
    }

    public Demand update(UUID demandPublicId, DemandUpdateRequest request) {
        Demand demand = demandRepository.findByPublicId(demandPublicId);



        return demand;
    }
}
