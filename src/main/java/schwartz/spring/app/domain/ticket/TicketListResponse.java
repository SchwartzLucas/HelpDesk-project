package schwartz.spring.app.domain.ticket;

import schwartz.spring.Utils.Utils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public record TicketListResponse(
        String public_code,
        String title,
        String description,
        Integer priority,
        Integer status,
        Integer category,
        LocalDateTime sla_expiration,
        LocalDateTime created_date,
        LocalDateTime updated_date,
        UUID id
) {
    public static List<TicketListResponse> from(List<Ticket> ticket) {
        if(Utils.isEmpty(ticket) || ticket.isEmpty()){
            return Collections.emptyList();
        }
        return ticket.stream().map(t -> new TicketListResponse(
                        t.getPublicCode(),
                        t.getTitle(),
                        t.getDescription(),
                        t.getPriority(),
                        t.getStatus(),
                        t.getCategory(),
                        t.getSlaExpiration(),
                        t.getCreatedDate(),
                        t.getUpdatedDate(),
                        t.getPublicId()
                ))
                .toList();
    }
}
