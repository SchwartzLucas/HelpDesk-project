package schwartz.spring.app.domain.demand;

import schwartz.spring.Utils.Filter;

import java.util.List;

public record DemandListRequest(
        String public_code,
        String user,
        String title,
        DemandStatus status,
        List<Filter> filters
) {
}
