package schwartz.spring.app.domain.demand;

public record DemandUpdateResponse(

) {
    public static DemandUpdateResponse from(Demand demand){
        return  new DemandUpdateResponse(

        );
    }
}
