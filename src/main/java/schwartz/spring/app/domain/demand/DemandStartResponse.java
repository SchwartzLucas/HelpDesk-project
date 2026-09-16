package schwartz.spring.app.domain.demand;

import java.time.Instant;

public record DemandStartResponse(
        Instant now,
        Integer status
) {
    public static DemandStartResponse from(Demand demand) {
        return new DemandStartResponse(
                demand.getStartedTime(),
                demand.getDemandStatus()
        );
    }
}
