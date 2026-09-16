package schwartz.spring.app.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import schwartz.spring.app.domain.team.TeamCreateRequest;
import schwartz.spring.app.domain.team.TeamResponse;
import schwartz.spring.app.services.TeamService;
import schwartz.spring.auth.domain.user.UserResponse;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping("/create")
    public ResponseEntity<TeamResponse> create(@RequestBody @Validated TeamCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teamService.create(request));
    }

    @GetMapping("/list")
    public ResponseEntity<List<TeamResponse>> list() {
        return ResponseEntity.ok(teamService.list());
    }

    @GetMapping("/{teamId}/members")
    public ResponseEntity<List<UserResponse>> members(@PathVariable Long teamId) {
        return ResponseEntity.ok(teamService.members(teamId));
    }
}