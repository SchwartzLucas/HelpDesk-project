package schwartz.spring.app.domain.ticket;


import java.time.LocalDateTime;

public record TicketUpdateResponse(
        String title,
        String description,
        Integer priority,
        Integer status,
        Long team_id,
        Long responsable_id,
        LocalDateTime updated_date
) {
    public static TicketUpdateResponse from(Ticket ticket){
        return new TicketUpdateResponse(
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getPriority(),
                ticket.getStatus(),
                ticket.getTeamId(),
                ticket.getResponsibleId(),
                ticket.getUpdatedDate()
        );
    }
}
