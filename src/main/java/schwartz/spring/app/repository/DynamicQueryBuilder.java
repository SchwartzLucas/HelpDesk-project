package schwartz.spring.app.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import schwartz.spring.Utils.Filter;
import schwartz.spring.Utils.Utils;
import java.time.Instant;

@Component
public class DynamicQueryBuilder {

    public <T> Specification<T> dateTimeQuery(
            Filter filter,
            String rootName,
            Specification<T> spec
    ) {
        if (Utils.isEmpty(filter)
                || Utils.isEmpty(rootName)
                || Utils.isEmpty(filter.operator())
                || Utils.isEmpty(filter.values())) {
            return spec;
        }

        switch (filter.operator()) {

            case GREATER_THAN_OR_EQUAL -> {
                Instant value = Instant.parse(filter.values().getFirst());

                spec = spec.and((root, query, cb) ->
                        cb.greaterThanOrEqualTo(
                                root.get(rootName),
                                value
                        )
                );
            }

            case GREATER_THAN -> {
                Instant value = Instant.parse(filter.values().getFirst());

                spec = spec.and((root, query, cb) ->
                        cb.greaterThan(
                                root.get(rootName),
                                value
                        )
                );
            }

            case LESS_THAN_OR_EQUAL -> {
                Instant value = Instant.parse(filter.values().getFirst());

                spec = spec.and((root, query, cb) ->
                        cb.lessThanOrEqualTo(
                                root.get(rootName),
                                value
                        )
                );
            }

            case LESS_THAN -> {
                Instant value = Instant.parse(filter.values().getFirst());

                spec = spec.and((root, query, cb) ->
                        cb.lessThan(
                                root.get(rootName),
                                value
                        )
                );
            }

            case EQUALS -> {
                Instant value = Instant.parse(filter.values().getFirst());

                spec = spec.and((root, query, cb) ->
                        cb.equal(
                                root.get(rootName),
                                value
                        )
                );
            }

            case BETWEEN -> {
                if (filter.values().size() < 2) {
                    // TODO: retornar erro
                    return spec;
                }

                Instant lowerValue = Instant.parse(filter.values().getFirst());
                Instant value = Instant.parse(filter.values().get(1));

                spec = spec.and((root, query, cb) ->
                        cb.between(
                                root.get(rootName),
                                lowerValue,
                                value
                        )
                );
            }
        }

        return spec;
    }
}
