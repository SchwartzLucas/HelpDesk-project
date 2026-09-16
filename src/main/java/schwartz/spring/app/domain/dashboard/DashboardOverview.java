package schwartz.spring.app.domain.dashboard;

public record DashboardOverview(
        long today,
        long week,
        long month,
        long overdue,
        long inProgress,
        long done,
        long total
) {
}