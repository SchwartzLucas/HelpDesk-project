package schwartz.spring.app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import schwartz.spring.Utils.Utils;
import schwartz.spring.app.domain.demand.*;
import schwartz.spring.app.services.DemandService;

import java.util.UUID;

@RestController
@RequestMapping("/demand")
public class DemandController {

    private final DemandService demandService;

    public DemandController(DemandService demandService) {
        this.demandService = demandService;
    }

    @PostMapping("/create")
    public ResponseEntity<DemandCreateResponse> create(@RequestBody @Validated DemandCreateRequest request) {
        Demand demand = demandService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DemandCreateResponse.from(demand));
    }

    @PostMapping("/stop/{demand_public_id}")
    public ResponseEntity<DemandStopResponse> stop(@PathVariable UUID demand_public_id) {
        if (Utils.isEmpty(demand_public_id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
        Demand demand = demandService.stop(demand_public_id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(DemandStopResponse.from(demand));
    }

    @PostMapping("/start/{demand_public_id}")
    public ResponseEntity<DemandStartResponse> start(@PathVariable UUID demand_public_id) {
        if (Utils.isEmpty(demand_public_id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
        Demand demand = demandService.start(demand_public_id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(DemandStartResponse.from(demand));
    }

    @PostMapping("/update/{demand_public_id}")
    public ResponseEntity<DemandUpdateResponse> update(@PathVariable UUID demand_public_id,
                                                       @RequestBody(required = false) DemandUpdateRequest request) {
        if (Utils.isEmpty(demand_public_id) || Utils.isEmpty(request)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
        Demand demand = demandService.update(demand_public_id, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(DemandUpdateResponse.from(demand));
    }
}
