package schwartz.spring.app.domain.demand;

import java.util.List;

public record DemandListResponse(

) {
    public static List<DemandListResponse> from(List<Demand> demand){
        return demand.stream().map(d -> new DemandListResponse (

        )).toList();
    }
}
