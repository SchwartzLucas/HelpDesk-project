package schwartz.spring.app.domain.demand;

public record DemandListByPublicIdResponse(
        String title,
        String description,
        DemandStatus status
) {
    public static DemandListByPublicIdResponse from(Demand d){
        return new DemandListByPublicIdResponse(
                d.getTitle(),
                d.getDescription(),
                d.getDemandStatus()
        );
    }
}
