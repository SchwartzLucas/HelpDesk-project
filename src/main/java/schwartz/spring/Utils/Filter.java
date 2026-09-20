package schwartz.spring.Utils;




import java.util.List;

public record Filter(
        String property,
        FilterOperator operator,
        List<String> values
) {
}
