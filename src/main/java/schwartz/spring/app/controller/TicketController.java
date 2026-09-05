package schwartz.spring.app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import schwartz.spring.app.domain.ticket.*;
import schwartz.spring.app.services.TicketService;

import java.util.List;

@RestController
@RequestMapping("/api/ticket")
public class TicketController {


    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }


    @PostMapping("/create")
    public ResponseEntity<TicketCreateResponse> create(@RequestBody @Validated TicketCreateRequest request) {
        Ticket ticket = ticketService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TicketCreateResponse.from(ticket));
    }

    @PostMapping("/update")
    public ResponseEntity<TicketUpdateResponse> update(@RequestBody @Validated TicketUpdateRequest request) {
        Ticket ticket = ticketService.update(request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(TicketUpdateResponse.from(ticket));
    }

    @GetMapping("/list")
    public ResponseEntity<List<TicketListResponse>> list(@RequestBody(required = false) TicketListRequest request) {
        List<Ticket> ticket = ticketService.list(request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(TicketListResponse.from(ticket));
    }
}
