package schwartz.spring.app.domain.demand;

import java.time.Instant;
import java.util.UUID;

public record DemandCreateResponse(
        UUID id,
        String public_code,
        String tile,
        String description,
        String user_name,
        Instant create_time,
        Integer demand_status

) {
    public static DemandCreateResponse from(Demand demand){
        return new DemandCreateResponse(
                demand.getPublicId(),
                demand.getPublicCode(),
                demand.getTitle(),
                demand.getDescription(),
                demand.getUserName() ,
                demand.getCreateTime(),
                demand.getDemandStatus()
        );
    }
}
