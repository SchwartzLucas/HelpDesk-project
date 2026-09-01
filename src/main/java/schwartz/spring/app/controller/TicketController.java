package schwartz.spring.app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import schwartz.spring.app.domain.ticket.*;
import schwartz.spring.app.services.TicketService;

@RestController
@RequestMapping("/api/ticket")
public class TicketController {


    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }


    @PostMapping("/create")
    public ResponseEntity<TicketCreateResponse> create(@RequestBody @Validated TicketCreateRequest request){
        Ticket ticket = ticketService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TicketCreateResponse.from(ticket));
    }

    @PostMapping("/update")
    public ResponseEntity<TicketUpdateResponse> update(@RequestBody @Validated TicketUpdateRequest request){
        Ticket ticket = ticketService.update(request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(TicketUpdateResponse.from(ticket));
    }

    @PostMapping("/list")
public ResponseEntity<TicketListResponse> list(){
        Ticket ticket = ticketService.list();
        return ResponseEntity.status(HttpStatus.OK)
                .body(TicketListResponse.from(ticket));
    }
}
