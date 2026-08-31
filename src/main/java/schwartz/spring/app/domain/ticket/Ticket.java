package schwartz.spring.app.domain.ticket;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "ticket")
@Table(
        name = "ticket",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_ticket_public_id",
                        columnNames = "public_id"
                ),
                @UniqueConstraint(
                        name = "uk_ticket_public_code",
                        columnNames = "public_code"
                )
        }
)
@Getter
@Setter
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "title", nullable = false, length = 250)
    private String title;
    @Column(name = "description", nullable = false, length = 5000)
    private String description;
    @ColumnDefault("0")
    @Column(name = "priority", nullable = false)
    private Integer priority;
    @ColumnDefault("0")
    @Column(name = "status", nullable = false)
    private Integer status;
    @ColumnDefault("'SEM CATEGORIA'")
    @Column(name = "category", length = 100)
    private String category;
    @Column(name = "client_id", nullable = false, length = 36)
    private Long clientId;
    @Column(name = "team_id", nullable = false, length = 36)
    private Long teamId;
    @Column(name = "responsable_id")
    private Long responsibleId;
    @Column(name = "sla_expiration", nullable = false)
    private LocalDateTime slaExpiration;
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;
    @ColumnDefault("(uuid_to_bin(uuid()))")
    @Column(
            name = "public_id",
            nullable = false,
            unique = true,
            columnDefinition = "BINARY(16)"
    )
    private UUID publicId;
    @Column(
            name = "public_code",
            unique = true,
            length = 30
    )
    private String publicCode;

}

