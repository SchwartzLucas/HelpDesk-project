package schwartz.spring.app.domain.demand;

import java.util.UUID;

public record DemandCreateResponse(
        UUID id,
        String public_code,
        String tile,
        String description

) {
    public static DemandCreateResponse from(Demand demand){
        return new DemandCreateResponse(
                demand.getPublicId(),
                demand.getPublicCode(),
                demand.getTitle(),
                demand.getDescription()
        );
    }
}
