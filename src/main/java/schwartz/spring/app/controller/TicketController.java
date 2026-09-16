package schwartz.spring.app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import schwartz.spring.app.domain.ticket.*;
import schwartz.spring.app.services.TicketService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/ticket")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping("/create")
    public ResponseEntity<List<TicketResponse>> create(@RequestBody @Validated TicketCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ticketService.create(request));
    }

    @PostMapping("/update")
    public ResponseEntity<List<TicketResponse>> update(@RequestBody @Validated TicketUpdateRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ticketService.update(request));
    }

    @GetMapping("/list")
    public ResponseEntity<List<TicketResponse>> list(
            @RequestParam(required = false) String public_code,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer category,
            @RequestParam(required = false) Integer priority,
            @RequestParam(required = false) Long client_id,
            @RequestParam(required = false) Long team_id,
            @RequestParam(required = false) Long responsable_id,
            @RequestParam(required = false) LocalDate deadline_from,
            @RequestParam(required = false) LocalDate deadline_to,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) LocalDate created_date
    ) {
        TicketListRequest request = new TicketListRequest(
                public_code, title, status, category, priority,
                client_id, team_id, responsable_id,
                deadline_from, deadline_to, period, created_date
        );
        return ResponseEntity.status(HttpStatus.OK)
                .body(ticketService.list(request));
    }
}