package schwartz.spring.app.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import schwartz.spring.app.domain.team.Team;
import schwartz.spring.app.domain.team.TeamCreateRequest;
import schwartz.spring.app.domain.team.TeamResponse;
import schwartz.spring.app.infra.PublicIdGenerator;
import schwartz.spring.app.repository.TeamRepository;
import schwartz.spring.auth.domain.user.User;
import schwartz.spring.auth.domain.user.UserResponse;
import schwartz.spring.auth.domain.user.UserRole;
import schwartz.spring.auth.repository.user.UserRepository;
import schwartz.spring.auth.services.UserService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final PublicIdGenerator publicIdGenerator;
    private final UserService userService;

    public TeamService(
            TeamRepository teamRepository,
            UserRepository userRepository,
            PublicIdGenerator publicIdGenerator,
            UserService userService
    ) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.publicIdGenerator = publicIdGenerator;
        this.userService = userService;
    }

    public TeamResponse create(TeamCreateRequest request) {
        User auth = userService.getAuthenticatedUser();
        if (!UserRole.ADMIN_USER.equals(auth.getRole()) && !UserRole.SUPPORT_USER.equals(auth.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas administradores podem criar times");
        }

        Team team = new Team();
        team.setPublicId(publicIdGenerator.generate());
        team.setName(request.name());
        team.setDescription(request.description() != null ? request.description() : "");
        team.setManagerId(request.managerId());
        teamRepository.saveAndFlush(team);

        team.setPublicCode(String.format("TEAM-%08d", team.getId()));
        teamRepository.save(team);

        return toResponse(team, 0L, managerName(team.getManagerId()));
    }

    public List<TeamResponse> list() {
        List<Team> teams = teamRepository.findAll();
        List<User> users = userRepository.findAll();

        Map<Long, Long> countByTeam = users.stream()
                .filter(u -> u.getTeamId() != null)
                .collect(Collectors.groupingBy(User::getTeamId, Collectors.counting()));
        Map<Long, String> names = users.stream()
                .collect(Collectors.toMap(User::getId, User::getName, (a, b) -> a));

        return teams.stream()
                .map(t -> toResponse(t, countByTeam.getOrDefault(t.getId(), 0L), names.getOrDefault(t.getManagerId(), "")))
                .toList();
    }

    public List<UserResponse> members(Long teamId) {
        String teamName = teamRepository.findById(teamId).map(Team::getName).orElse("");
        return userRepository.findAllByTeamId(teamId).stream()
                .map(u -> UserResponse.from(u, teamName))
                .toList();
    }

    private TeamResponse toResponse(Team t, Long count, String managerName) {
        return new TeamResponse(
                t.getPublicId(),
                t.getId(),
                t.getPublicCode(),
                t.getName(),
                t.getDescription(),
                t.getManagerId(),
                managerName,
                count
        );
    }

    private String managerName(Long id) {
        return userRepository.findById(id).map(User::getName).orElse("");
    }
}