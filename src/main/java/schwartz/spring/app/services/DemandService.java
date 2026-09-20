package schwartz.spring.app.services;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import schwartz.spring.Utils.Filter;
import schwartz.spring.Utils.Utils;
import schwartz.spring.app.domain.demand.*;
import schwartz.spring.app.infra.PublicIdGenerator;
import schwartz.spring.app.repository.DemandRepository;
import schwartz.spring.app.repository.DynamicQueryBuilder;
import schwartz.spring.app.domain.user.User;
import schwartz.spring.app.repository.UserRepository;

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

    public DemandService(PublicIdGenerator publicIdGenerator, DemandRepository demandRepository,
                         UserRepository userRepository, DynamicQueryBuilder db) {
        this.publicIdGenerator = publicIdGenerator;
        this.demandRepository = demandRepository;
        this.userRepository = userRepository;
        DB = db;
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
        demand.setDemandStatus(DemandStatus.CREATED);
        demandRepository.saveAndFlush(demand);
        demand.setPublicCode(String.format(
                        "DEM-%08d", demand.getUserDemandId()
                )
        );
        demandRepository.save(demand);
        return demand;
    }


    @Transactional
    @Modifying(clearAutomatically = true)
    public Demand update(UUID demandPublicId, DemandUpdateRequest request) {
        Demand demand = demandRepository.findByPublicId(demandPublicId);
        if (Utils.isEmpty(demand)) {
            return null;
        }
        if (!Utils.isEmpty(request.status())) {
            final boolean ACTIVE_OR_CANCELED = request.status().equals(ACTIVE) || request.status().equals(CANCELED);
            switch (demand.getDemandStatus()) {
                case CREATED -> { // created demand
                    if (ACTIVE_OR_CANCELED) {
                        demand.setDemandStatus(request.status());
                    }
                    // TODO EXCEPTION: created demand can only be updated to active or canceled
                }
                case ACTIVE -> { // actual active demand
                    if (request.status().equals(STOPPED) || request.status().equals(FINISHED)) {
                        start(demand);
                    }
                    // TODO EXCEPTION: active demand can only be updated to stopped or finished
                }
                case STOPPED -> { // stopped demand
                    if (request.status().equals(CREATED) || request.status().equals(FINISHED)) {
                        stop(demand);
                        demand.setDemandStatus(request.status());
                    }
                    // TODO EXCEPTION: stopped demand can only be updated to active or finished
                }
                case FINISHED -> { // finished demand
                    if (request.status().equals(CANCELED) || request.status().equals(ACTIVE)) {
                        demand.setDemandStatus(request.status());
                    }
                    // TODO EXCEPTION: finished demand can only be updated to canceled or active
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
        demandRepository.saveAndFlush(demand);
        return demand;
    }

    private void stop(Demand demand) {
        Instant now = Instant.now();
        demandRepository.updateStoppedTime(now, demand.getPublicId());
        demand.setStoppedTime(now);
        demand.setDemandStatus(DemandStatus.STOPPED);
    }

    // TODO ver como fazer exceptions
    private void start(Demand demand) {
        Instant now = Instant.now();
        demandRepository.updateStartTime(now, demand.getPublicId());
        demand.setStartedTime(now);
        demand.setDemandStatus(ACTIVE);
    }

    public List<Demand> list(DemandListRequest request) {
        if (Utils.isEmpty(request)) {
            return demandRepository.findAll();
        }
        Filter createTimeFilter = request.filters().stream().filter(f -> f.property().equals("create_time")).findFirst().orElse(null);
        Filter startedTimeFilter = request.filters().stream().filter(f -> f.property().equals("started_time")).findFirst().orElse(null);
        Filter stoppedTimeFilter = request.filters().stream().filter(f -> f.property().equals("stopped_time")).findFirst().orElse(null);
        Filter finishedTimeFilter = request.filters().stream().filter(f -> f.property().equals("finished_time")).findFirst().orElse(null);

        Specification<Demand> spec = Specification.where((root, query, cb) -> cb.conjunction());
        if (!Utils.isEmpty(request.public_code())) {
            spec = spec.and(((root, query, cb) ->
                            cb.like(root.get("public_code"), request.public_code())
                    )
            );
        }

        if (!Utils.isEmpty(request.title())) {
            spec = spec.and(((root, query, cb) ->
                    cb.like(root.get("title"), request.title()))
            );

        }
        if (!Utils.isEmpty(request.user())) {
            User user = userRepository.findByLogin(request.user());
            if (!Utils.isEmpty(user)) {

                spec = spec.and(((root, query, cb) ->
                        cb.equal(root.get("user_id"), user.getPublicId()))
                );
            }

        }

        if (!Utils.isEmpty(request.status())) {
            spec = spec.and(((root, query, cb) ->
                    cb.equal(root.get("demand_status"), request.status()))
            );

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
}
