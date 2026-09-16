package schwartz.spring.app.domain.dashboard;

import java.util.List;

public record TeamDashboardRow(
        String teamName,
        Long teamId,
        List<DashboardGroup> members
) {
}