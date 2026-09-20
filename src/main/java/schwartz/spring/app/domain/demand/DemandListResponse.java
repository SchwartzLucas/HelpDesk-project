package schwartz.spring.app.domain.demand;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record DemandListResponse(
        String user_name,
        String public_code,
        UUID public_id,
        String title,
        String description,
        Instant create_time,
        Instant started_time,
        Instant stopped_time,
        Instant finish_time
        ) {
    public static List<DemandListResponse> from(List<Demand> demand){
        return demand.stream().map(d -> new DemandListResponse (
                d.getUser_name(),
                d.getPublicCode(),
                d.getPublicId(),
                d.getTitle(),
                d.getDescription(),
                d.getCreateTime(),
                d.getStartedTime(),
                d.getStoppedTime(),
                d.getFinishTime()
        )).toList();
    }
}
