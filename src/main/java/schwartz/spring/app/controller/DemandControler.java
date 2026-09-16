package schwartz.spring.app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import schwartz.spring.app.domain.demand.Demand;
import schwartz.spring.app.domain.demand.DemandCreateRequest;
import schwartz.spring.app.domain.demand.DemandCreateResponse;
import schwartz.spring.app.services.DemandService;

@RestController
@RequestMapping("/demand")
public class DemandControler {

    private final DemandService demandService;

    public DemandControler(DemandService demandService) {
        this.demandService = demandService;
    }

    @PostMapping("/create")
    public ResponseEntity<DemandCreateResponse> create(@RequestBody @Validated DemandCreateRequest request){
        Demand demand = demandService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DemandCreateResponse.from(demand));
    }
}
