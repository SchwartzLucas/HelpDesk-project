package schwartz.spring.app.domain.demand;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "demand_status_history",
        indexes = {
                @Index(
                        name = "idx_demand_status_history_demand_time",
                        columnList = "demand_id, changed_at"
                )
        }
)
public class DemandStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "demand_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_demand_status_history_demand")
    )
    private Demand demand;

    @Column(name = "previous_status")
    private Integer previousStatusCode;

    @Column(name = "new_status", nullable = false)
    private int newStatusCode;

    @Column(name = "changed_at", nullable = false)
    private Instant changedAt;

    @Column(name = "changed_by")
    private UUID changedBy;

    public DemandStatus getPreviousStatus() {
        return previousStatusCode == null
                ? null
                : DemandStatus.fromCode(previousStatusCode);
    }

    public void setPreviousStatus(DemandStatus status) {
        this.previousStatusCode =
                status == null ? null : status.getCode();
    }

    public DemandStatus getNewStatus() {
        return DemandStatus.fromCode(newStatusCode);
    }

    public void setNewStatus(DemandStatus status) {
        if (status == null) {
            throw new IllegalArgumentException(
                    "O novo status não pode ser nulo"
            );
        }

        this.newStatusCode = status.getCode();
    }
}