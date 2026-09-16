package schwartz.spring.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import schwartz.spring.app.domain.dashboard.DashboardGroup;
import schwartz.spring.app.domain.dashboard.DashboardOverview;
import schwartz.spring.app.domain.dashboard.TeamDashboardRow;
import schwartz.spring.app.domain.ticket.TicketResponse;
import schwartz.spring.app.services.DashboardService;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/overview")
    public ResponseEntity<DashboardOverview> overview() {
        return ResponseEntity.ok(dashboardService.overview());
    }

    @GetMapping("/tickets")
    public ResponseEntity<List<TicketResponse>> tickets(@RequestParam(required = false) String period) {
        return ResponseEntity.ok(dashboardService.ticketsByPeriod(period));
    }

    @GetMapping("/by-client")
    public ResponseEntity<List<DashboardGroup>> byClient() {
        return ResponseEntity.ok(dashboardService.byClient());
    }

    @GetMapping("/by-team")
    public ResponseEntity<List<TeamDashboardRow>> byTeam() {
        return ResponseEntity.ok(dashboardService.byTeam());
    }
}