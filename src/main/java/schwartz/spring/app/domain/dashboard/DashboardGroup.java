package schwartz.spring.app.domain.dashboard;

import schwartz.spring.app.domain.ticket.TicketResponse;

import java.util.List;

public record DashboardGroup(
        String name,
        Long id,
        long count,
        long done,
        long overdue,
        List<TicketResponse> tickets
) {
}