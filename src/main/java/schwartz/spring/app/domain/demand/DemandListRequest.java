package schwartz.spring.app.domain.demand;

import java.time.Instant;
import java.util.UUID;

public record DemandListRequest(
        String public_code,
        UUID user,
        String tile,
        String description,
        DemandStatus status,
        Instant create_time,
        Instant stopped_time,
        Instant finished_time,
        Instant started_time
) {
}
