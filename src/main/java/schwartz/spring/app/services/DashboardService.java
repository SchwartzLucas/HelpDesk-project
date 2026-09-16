package schwartz.spring.app.services;

import org.springframework.stereotype.Service;
import schwartz.spring.app.domain.dashboard.DashboardGroup;
import schwartz.spring.app.domain.dashboard.DashboardOverview;
import schwartz.spring.app.domain.dashboard.TeamDashboardRow;
import schwartz.spring.app.domain.ticket.TicketListRequest;
import schwartz.spring.app.domain.ticket.TicketResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final TicketService ticketService;

    public DashboardService(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    public DashboardOverview overview() {
        List<TicketResponse> all = all();
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime tomorrow = today.plusDays(1).atStartOfDay();
        LocalDateTime weekEnd = today.plusDays(7).atStartOfDay();
        LocalDateTime nextMonth = today.withDayOfMonth(1).plusMonths(1).atStartOfDay();

        long total = all.size();
        long done = all.stream().filter(DashboardService::isDone).count();
        long todayCount = countDueIn(all, todayStart, tomorrow);
        long weekCount = countDueIn(all, todayStart, weekEnd);
        long monthCount = countDueIn(all, todayStart, nextMonth);
        long overdue = all.stream()
                .filter(t -> !isDone(t))
                .filter(t -> t.deadline() != null && t.deadline().isBefore(todayStart))
                .count();
        long inProgress = total - done;

        return new DashboardOverview(todayCount, weekCount, monthCount, overdue, inProgress, done, total);
    }

    public List<DashboardGroup> byClient() {
        List<TicketResponse> all = all();
        Map<Long, List<TicketResponse>> groups = all.stream()
                .collect(Collectors.groupingBy(t -> t.clientId() != null ? t.clientId() : 0L));

        return groups.entrySet().stream()
                .map(e -> {
                    Long key = e.getKey();
                    String name = key == 0L ? "Sem cliente"
                            : firstNonEmpty(e.getValue(), TicketResponse::clientName, "Cliente");
                    return toGroup(name, key, e.getValue());
                })
                .sorted(Comparator.comparingLong(DashboardGroup::count).reversed())
                .toList();
    }

    public List<TeamDashboardRow> byTeam() {
        List<TicketResponse> all = all();
        Map<Long, List<TicketResponse>> byTeam = all.stream()
                .collect(Collectors.groupingBy(t -> t.teamId() != null ? t.teamId() : 0L));

        return byTeam.entrySet().stream()
                .map(e -> {
                    Long teamId = e.getKey();
                    List<TicketResponse> teamTickets = e.getValue();
                    String teamName = teamId == 0L ? "Sem time"
                            : firstNonEmpty(teamTickets, TicketResponse::teamName, "Time");

                    Map<Long, List<TicketResponse>> byPerson = teamTickets.stream()
                            .collect(Collectors.groupingBy(t -> t.responsableId() != null ? t.responsableId() : 0L));

                    List<DashboardGroup> members = byPerson.entrySet().stream()
                            .map(me -> {
                                Long personId = me.getKey();
                                String personName = personId == 0L ? "Não atribuído"
                                        : firstNonEmpty(me.getValue(), TicketResponse::responsableName, "Pessoa");
                                return toGroup(personName, personId, me.getValue());
                            })
                            .sorted(Comparator.comparingLong(DashboardGroup::count).reversed())
                            .toList();

                    return new TeamDashboardRow(teamName, teamId == 0L ? null : teamId, members);
                })
                .sorted(Comparator.comparing(TeamDashboardRow::teamName))
                .toList();
    }

    public List<TicketResponse> ticketsByPeriod(String period) {
        return ticketService.list(new TicketListRequest(
                null, null, null, null, null, null, null, null,
                null, null, period, null
        ));
    }

    // ------------------------------------------------------------

    private List<TicketResponse> all() {
        return ticketsByPeriod("all");
    }

    private static String firstNonEmpty(
            List<TicketResponse> list,
            java.util.function.Function<TicketResponse, String> extractor,
            String fallback
    ) {
        return list.stream().map(extractor)
                .filter(n -> n != null && !n.isBlank())
                .findFirst()
                .orElse(fallback);
    }

    private static boolean isDone(TicketResponse t) {
        // 4 = concluído, 5 = fechado
        return t.status() != null && t.status() >= 4;
    }

    private static long countDueIn(List<TicketResponse> list, LocalDateTime from, LocalDateTime to) {
        return list.stream()
                .filter(t -> !isDone(t))
                .filter(t -> t.deadline() != null)
                .filter(t -> !t.deadline().isBefore(from) && t.deadline().isBefore(to))
                .count();
    }

    private DashboardGroup toGroup(String name, Long id, List<TicketResponse> tickets) {
        long done = tickets.stream().filter(DashboardService::isDone).count();
        long overdue = tickets.stream()
                .filter(t -> !isDone(t))
                .filter(t -> t.deadline() != null && t.deadline().isBefore(LocalDate.now().atStartOfDay()))
                .count();
        return new DashboardGroup(name, id, tickets.size(), done, overdue, tickets);
    }
}