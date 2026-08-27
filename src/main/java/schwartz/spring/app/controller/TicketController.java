package schwartz.spring.app.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import schwartz.spring.app.domain.ticket.Ticket;
import schwartz.spring.app.domain.ticket.TicketCreateRequest;
import schwartz.spring.app.domain.ticket.TicketResponse;
import schwartz.spring.app.services.ClientService;
import schwartz.spring.app.services.TicketService;

@RestController
@RequestMapping("/api/ticket")
public class TicketController {

    @Autowired
    private TicketService ticketService;


    @PostMapping("/create")
    public ResponseEntity<TicketResponse> create(@RequestBody @Validated TicketCreateRequest request){
        Ticket ticket = ticketService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TicketResponse.from(ticket));
    }

}
