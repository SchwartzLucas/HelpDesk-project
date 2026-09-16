package schwartz.spring.app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import schwartz.spring.app.domain.ticket.TimeEntryResponse;
import schwartz.spring.app.domain.ticket.TimeStartRequest;
import schwartz.spring.app.services.TimeTrackingService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/times")
public class TimeTrackingController {

    private final TimeTrackingService timeTrackingService;

    public TimeTrackingController(TimeTrackingService timeTrackingService) {
        this.timeTrackingService = timeTrackingService;
    }

    @PostMapping("/start")
    public ResponseEntity<TimeEntryResponse> start(@RequestBody TimeStartRequest request) {
        return ResponseEntity.ok(timeTrackingService.start(request.ticketId()));
    }

    @PostMapping("/pause")
    public ResponseEntity<TimeEntryResponse> pause(@RequestBody TimeStartRequest request) {
        return ResponseEntity.ok(timeTrackingService.pause(request.ticketId()));
    }

    @PostMapping("/resume")
    public ResponseEntity<TimeEntryResponse> resume(@RequestBody TimeStartRequest request) {
        return ResponseEntity.ok(timeTrackingService.resume(request.ticketId()));
    }

    @PostMapping("/stop")
    public ResponseEntity<TimeEntryResponse> stop(@RequestBody TimeStartRequest request) {
        return ResponseEntity.ok(timeTrackingService.stop(request.ticketId()));
    }

    @GetMapping("/active")
    public ResponseEntity<TimeEntryResponse> active() {
        TimeEntryResponse response = timeTrackingService.getActive();
        return response == null
                ? ResponseEntity.status(HttpStatus.NO_CONTENT).build()
                : ResponseEntity.ok(response);
    }

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<TimeEntryResponse>> listByTicket(@PathVariable UUID ticketId) {
        return ResponseEntity.ok(timeTrackingService.listByTicket(ticketId));
    }

    @GetMapping("/ticket/{ticketId}/total")
    public ResponseEntity<Long> total(@PathVariable UUID ticketId) {
        return ResponseEntity.ok(timeTrackingService.totalSeconds(ticketId));
    }
}