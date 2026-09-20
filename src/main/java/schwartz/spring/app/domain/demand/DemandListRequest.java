package schwartz.spring.app.domain.demand;

import schwartz.spring.Utils.Filter;

import java.util.List;
import java.util.UUID;

public record DemandListRequest(
        String public_code,
        UUID user,
        String title,
        DemandStatus status,
        List<Filter> filters
) {
}
