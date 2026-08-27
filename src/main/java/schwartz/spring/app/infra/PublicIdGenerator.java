package schwartz.spring.app.infra;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.UUID;

@Component
public class PublicIdGenerator {

    private final Clock clock;

    public PublicIdGenerator(Clock clock) {
        this.clock = clock;
    }

    public UUID generate() {
        return UUID.ofEpochMillis(
                clock.millis()
        );
    }
}
