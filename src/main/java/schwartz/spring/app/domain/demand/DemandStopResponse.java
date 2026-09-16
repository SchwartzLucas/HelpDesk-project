package schwartz.spring.app.domain.demand;

import java.time.Instant;

public record DemandStopResponse(
        Instant stopped_time,
        Integer status

) {
    public static DemandStopResponse from(Demand demand) {
        return new DemandStopResponse(
                demand.getStoppedTime(),
                demand.getDemandStatus()
        );
    }
}
