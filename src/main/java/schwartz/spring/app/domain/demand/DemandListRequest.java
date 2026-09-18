package schwartz.spring.app.domain.demand;

import schwartz.spring.Utils.Filter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record DemandListRequest(
        String public_code,
        UUID user,
        String tile,
        DemandStatus status,
        List<Filter> filters
) {
}
