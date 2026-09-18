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
import schwartz.spring.auth.domain.user.User;
import schwartz.spring.auth.repository.user.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static schwartz.spring.app.domain.demand.DemandStatus.*;

@Service
public class DemandService {
    private final PublicIdGenerator publicIdGenerator;
    private final DemandRepository demandRepository;
    private final UserRepository userRepository;
    private final jakarta.servlet.Filter filter;

    public DemandService(PublicIdGenerator publicIdGenerator, DemandRepository demandRepository, UserRepository userRepository, jakarta.servlet.Filter filter) {
        this.publicIdGenerator = publicIdGenerator;
        this.demandRepository = demandRepository;
        this.userRepository = userRepository;
        this.filter = filter;
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
        Specification<Demand> spec = Specification.where((root, query, cb) -> cb.conjunction());
        if (!Utils.isEmpty(request.public_code())) {
            spec = spec.and(((root, query, cb) ->
                            cb.equal(root.get("public_code"), request.public_code())
                    )
            );
        }

        if (!Utils.isEmpty(request.tile())) {
            spec = spec.and(((root, query, cb) ->
                    cb.like(root.get("title"), request.tile()))
            );

        }

        if (!Utils.isEmpty(request.user())) {
            spec = spec.and(((root, query, cb) ->
                    cb.equal(root.get("user_id"), request.user()))
            );

        }

        if (!Utils.isEmpty(createTimeFilter)) {

            if (!Utils.isEmpty(createTimeFilter.operator())) {
                Instant value;
                Instant lowerValue;
                if (createTimeFilter.values().size() == 2) {
                    lowerValue = Instant.parse(
                            createTimeFilter.values().getFirst()
                    );
                } else {
                    lowerValue = null;
                }
                value = Instant.parse(
                        createTimeFilter.values().get(1)
                );
                switch (createTimeFilter.operator()) {
                    case GREATER_THAN_OR_EQUAL -> {
                        spec = spec.and(((root, query, cb) ->
                                cb.greaterThanOrEqualTo(root.get("create_time"), value))
                        );
                    }
                    case GREATER_THAN -> {
                        spec = spec.and(((root, query, cb) ->
                                cb.greaterThan(root.get("create_time"), value))
                        );
                    }
                    case LESS_THAN_OR_EQUAL -> {
                        spec = spec.and(((root, query, cb) ->
                                cb.lessThanOrEqualTo(root.get("create_time"), value))
                        );
                    }
                    case LESS_THAN -> {
                        spec = spec.and(((root, query, cb) ->
                                cb.lessThan(root.get("create_time"), value))
                        );
                    }
                    case EQUALS -> {
                        spec = spec.and(((root, query, cb) ->
                                cb.equal(root.get("create_time"), value))
                        );
                    }
                    case BETWEEN -> {
                        if (Utils.isEmpty(lowerValue)) {
                            // TODO RETORNAR ERRO
                        }
                        spec = spec.and(((root, query, cb) ->
                                cb.between(root.get("create_time"), lowerValue, value))
                        );
                    }
                }
            }


        }

        if (!Utils.isEmpty(request.status())) {
            spec = spec.and(((root, query, cb) ->
                    cb.equal(root.get("demand_status"), request.status()))
            );

        }

        if (!Utils.isEmpty()) {
            spec = spec.and(((root, query, cb) ->
                    cb.equal())
            );

        }

        if (!Utils.isEmpty()) {
            spec = spec.and(((root, query, cb) ->
                    cb.equal())
            );

        }

        if (!Utils.isEmpty()) {
            spec = spec.and(((root, query, cb) ->
                    cb.equal())
            );

        }

        if (!Utils.isEmpty()) {
            spec = spec.and(((root, query, cb) ->
                    cb.equal())
            );

        }
        return demandRepository.findAll(spec);
    }
}
