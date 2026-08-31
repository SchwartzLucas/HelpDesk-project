package schwartz.spring.app.domain.ticket;


import java.time.LocalDateTime;

public record TicketUpdateResponse(
        String title,
        String description,
        Integer priority,
        Integer status,
        Long tema_id,
        Long responsable_id,
        LocalDateTime updated_date
) {
    public TicketUpdateResponse from(Ticket ticket){
        return new TicketUpdateResponse(
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getPriority(),
                ticket.getStatus(),
                ticket.getTeamId(),
                ticket.getResponsableId(),
                ticket.getUpdatedDate()
        );
    }
}
