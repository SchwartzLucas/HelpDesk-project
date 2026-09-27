package schwartz.spring.app.services;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import schwartz.spring.Exceptions.IllegalStatusException;
import schwartz.spring.Exceptions.InvalidDemandException;
import schwartz.spring.Utils.Filter;
import schwartz.spring.Utils.Utils;
import schwartz.spring.app.domain.demand.*;
import schwartz.spring.app.domain.user.User;
import schwartz.spring.app.infra.PublicIdGenerator;
import schwartz.spring.app.repository.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static schwartz.spring.app.domain.demand.DemandStatus.*;

@Service
public class DemandService {
    private final PublicIdGenerator publicIdGenerator;
    private final DemandRepository demandRepository;
    private final UserRepository userRepository;
    private final DynamicQueryBuilder DB;
    private final DemandStatusHistoryRepository historyRepository;
    private final DemandWorkIntervalRepository intervalRepository;
    private final UserService userService;

    public DemandService(PublicIdGenerator publicIdGenerator, DemandRepository demandRepository, UserRepository userRepository, DynamicQueryBuilder db, DemandStatusHistoryRepository historyRepository, UserService userService, DemandWorkIntervalRepository intervalRepository, UserService userService1) {
        this.publicIdGenerator = publicIdGenerator;
        this.demandRepository = demandRepository;
        this.userRepository = userRepository;
        this.DB = db;
        this.historyRepository = historyRepository;
        this.intervalRepository = intervalRepository;
        this.userService = userService1;
    }

    @Transactional
    public Demand create(DemandCreateRequest request) {
        UUID user_public_id = null;
        Long user_demand_id = null;
        String user_name = null;
        if (!Utils.isEmpty(request.user_id())) {
            User user = userRepository.findByPublicId(request.user_id());
            user_public_id = user.getPublicId();
            user_name = user.getLogin();
            user_demand_id = demandRepository.findMaxUserDemandID(user.getPublicId()) + 1;
        }
        Instant now = Instant.now();
        Demand demand = new Demand();
        demand.setPublicId(publicIdGenerator.generate());
        demand.setTitle(request.title());
        demand.setDescription(request.description());
        demand.setDemandStatus(DemandStatus.CREATED);
        demand.setCreateTime(now);
        demand.setUserId(user_public_id);
        demand.setUserDemandId(user_demand_id);
        demand.setUser_name(user_name);
        demandRepository.saveAndFlush(demand);
        demand.setPublicCode(String.format("DEM-%08d", demand.getUserDemandId()));
        demandRepository.save(demand);
        return demand;
    }


    @Transactional
    @Modifying(clearAutomatically = true)
    public Demand update(UUID demandPublicId, DemandUpdateRequest request) throws IllegalStatusException {
        Demand demand = demandRepository.findByPublicId(demandPublicId);
        Instant now = Instant.now();
        if (Utils.isEmpty(demand)) {
            return null;
        }
        if (!Utils.isEmpty(request.status())) {
            final boolean ACTIVE_OR_CANCELED = request.status().equals(ACTIVE) || request.status().equals(CANCELED);
            switch (demand.getDemandStatus()) {
                case CREATED -> { // created demand
                    switch (request.status()) {
                        case ACTIVE -> start(now, demand);
                        case FINISHED, REOPENED, STOPPED ->
                                throw new IllegalStatusException("Status cannot be " + request.status() + "when demand status" + "are: " + demand.getDemandStatus().name());
                    }
                    demand.setDemandStatus(request.status());
                }
                case ACTIVE -> { // actual active demand
                    switch (request.status()) {
                        case STOPPED -> stop(now, demand);
                        case FINISHED -> finish(now, demand);
                        case REOPENED, CREATED ->
                                throw new IllegalStatusException("Status cannot be " + request.status() + "when demand status" + "are: " + demand.getDemandStatus().name());
                    }
                    demand.setDemandStatus(request.status());
                }
                case STOPPED -> { // stopped demand
                    switch (request.status()) {
                        case FINISHED -> finish(now, demand);
                        case ACTIVE -> startNewInterval(now, demand);
                        case CANCELED -> stopWorkInterval(now, demand);
                        case REOPENED, CREATED ->
                                throw new IllegalStatusException("Status cannot be " + request.status() + "when demand status" + "are: " + demand.getDemandStatus().name());
                    }
                    demand.setDemandStatus(request.status());
                }
                case FINISHED -> { // finished demand
                    switch (request.status()) {
                        case REOPENED -> demand.setDemandStatus(request.status());
                        case ACTIVE, CREATED, STOPPED, CANCELED ->
                                throw new IllegalStatusException("Status cannot be " + request.status() + "when demand status" + "are: " + demand.getDemandStatus().name());

                    }
                    changeStatus(demand, request.status(), now);
                }
                case CANCELED -> { // canceled demand
                    if (request.status().equals(REOPENED)) {
                        demand.setDemandStatus(request.status());
                    }
                    // TODO EXCEPTION: canceled demand can only be updated to reopened
                }
                case REOPENED -> { // reopened demand}
                    if (ACTIVE_OR_CANCELED) {
                        demand.setDemandStatus(request.status());
                    }
                    // TODO EXCEPTION: reopened demand can only be updated to active or canceled

                }
            }
        }
        if (!Utils.isEmpty(request.title())) {
            demand.setTitle(request.title());
        }
        if (!Utils.isEmpty(request.description())) {
            demand.setDescription(request.description());
        }
        if (!Utils.isEmpty(request.user())) {
            User user = userRepository.findByPublicId(request.user());
            if (!Utils.isEmpty(user)) {
                demand.setUserId(request.user());
            }
        }
        demandRepository.save(demand);
        changeStatus(demand, request.status(), now);
        return demand;
    }

    private void start(Instant now, Demand demand) {
        if (demandRepository.updateStartTime(now, demand.getPublicId().toString()) > 0) {
            demand.setStartedTime(now);
            DemandWorkInterval interval = new DemandWorkInterval();
            interval.setDemand(demand);
            interval.setStartedAt(now);
            intervalRepository.save(interval);
        }
    }

    private void stop(Instant now, Demand demand) {
        DemandWorkInterval interval = intervalRepository.findFirstByDemand_IdAndEndedAtIsNull(demand.getId()).orElse(null);
        if (interval != null && demandRepository.updateStoppedTime(now, demand.getPublicId().toString()) > 0) {
            demand.setStoppedTime(now);
            interval.setEndedAt(now);
            intervalRepository.save(interval);
            Duration time = Duration.between(interval.getStartedAt(), interval.getEndedAt());
        }
    }

    private void finish(Instant now, Demand demand) {
        if (demandRepository.updateFinishedTime(now, demand.getPublicId().toString()) > 0) {
            demand.setFinishTime(now);
            Duration time = Duration.between(demand.getStartedTime(), demand.getFinishTime());
            long hours = time.toHours();
            long minutes = time.toMinutesPart();
            String timeSpent = String.format("%02dh : %02dm", hours, minutes);
            demand.setTimeSpent(timeSpent);
        }
    }

    private void startNewInterval(Instant now, Demand demand) {
        DemandWorkInterval interval = new DemandWorkInterval();
        interval.setDemand(demand);
        interval.setStartedAt(now);
        intervalRepository.save(interval);
    }

    private void stopWorkInterval(Instant now, Demand demand){
        DemandWorkInterval interval = intervalRepository.findFirstByDemand_IdAndEndedAtIsNull(demand.getId()).orElse(null);
        if(interval != null){
            interval.setEndedAt(now);
            intervalRepository.save(interval);
        }
    }

    public List<Demand> list(DemandListRequest request) {
        List<Demand> demands = demandRepository.findAll();
        if (Utils.isEmpty(request)) {
            for (Demand demand : demands) {
                if (Utils.isEmpty(demand.getUserId())) {
                    continue;
                }
                User user = userRepository.findByPublicId(demand.getUserId());
                demand.setUser_name(user.getLogin());
            }
            return demands;

        }
        Filter createTimeFilter = request.filters().stream().filter(f -> f.property().equals("create_time")).findFirst().orElse(null);
        Filter startedTimeFilter = request.filters().stream().filter(f -> f.property().equals("started_time")).findFirst().orElse(null);
        Filter stoppedTimeFilter = request.filters().stream().filter(f -> f.property().equals("stopped_time")).findFirst().orElse(null);
        Filter finishedTimeFilter = request.filters().stream().filter(f -> f.property().equals("finished_time")).findFirst().orElse(null);

        Specification<Demand> spec = Specification.where((root, query, cb) -> cb.conjunction());
        if (!Utils.isEmpty(request.public_code())) {
            spec = spec.and(((root, query, cb) -> cb.like(root.get("public_code"), request.public_code())));
        }

        if (!Utils.isEmpty(request.title())) {
            spec = spec.and(((root, query, cb) -> cb.like(root.get("title"), request.title())));

        }
        if (!Utils.isEmpty(request.user())) {
            User user = userRepository.findByLogin(request.user());
            if (!Utils.isEmpty(user)) {

                spec = spec.and(((root, query, cb) -> cb.equal(root.get("user_id"), user.getPublicId())));
            }

        }

        if (!Utils.isEmpty(request.status())) {
            spec = spec.and(((root, query, cb) -> cb.equal(root.get("demand_status"), request.status())));

        }

        if (!Utils.isEmpty(createTimeFilter)) {
            spec = DB.dateTimeQuery(createTimeFilter, "create_time", spec);
        }


        if (!Utils.isEmpty(startedTimeFilter)) {
            spec = DB.dateTimeQuery(startedTimeFilter, "started_time", spec);

        }

        if (!Utils.isEmpty(stoppedTimeFilter)) {
            spec = DB.dateTimeQuery(stoppedTimeFilter, "stopped_time", spec);

        }

        if (!Utils.isEmpty(finishedTimeFilter)) {
            spec = DB.dateTimeQuery(finishedTimeFilter, "finished_time", spec);

        }
        return demandRepository.findAll(spec);
    }

    public Demand listByPublicId(UUID publicId) throws InvalidDemandException {
        if (Utils.isEmpty(publicId)) {
            throw new InvalidDemandException("Invalid PublicId");
        }
        Demand demand = demandRepository.findByPublicId(publicId);
        if (Utils.isEmpty(demand)) {
            throw new InvalidDemandException("Demand not exists anymore");
        }
        if (!Utils.isEmpty(demand.getUserId())) {
            User user = userRepository.findByPublicId(demand.getUserId());
            if (!Utils.isEmpty(user)) {
                demand.setUser_name(user.getLogin());
            }
        }
        return demand;
    }


    // TODO MELHORAR -> fazer adicionar ao time_spent -> realizar cálculo por tempos...
    @Transactional
    public void changeStatus(Demand demand, DemandStatus newStatus, Instant now) {
        User request_user = userService.getAuthenticatedUser();
        DemandStatus previousStatus = demand.getDemandStatus();
        if (previousStatus == newStatus) {
            return;
        }
        DemandStatusHistory history = new DemandStatusHistory();
        history.setDemand(demand);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(newStatus);
        history.setChangedAt(now);
        history.setChangedBy(request_user.getPublicId());
        historyRepository.save(history);
    }
}
