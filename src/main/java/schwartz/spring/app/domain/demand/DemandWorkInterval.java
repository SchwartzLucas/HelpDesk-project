package schwartz.spring.app.domain.demand;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "demand_work_interval",
        indexes = {
                @Index(
                        name = "idx_demand_work_interval_demand",
                        columnList = "demand_id"
                )
        }
)
public class DemandWorkInterval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "demand_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_demand_work_interval_demand"
            )
    )
    private Demand demand;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "ended_at")
    private Instant endedAt;
}