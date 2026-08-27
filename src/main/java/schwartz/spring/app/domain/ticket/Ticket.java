package schwartz.spring.app.domain.ticket;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Table(name = "ticket")
@Entity(name = "ticket")
@AllArgsConstructor
@NoArgsConstructor
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
    private Byte priority;
    @ColumnDefault("0")
    @Column(name = "status", nullable = false)
    private Byte status;
    @ColumnDefault("'SEM CATEGORIA'")
    @Column(name = "category", length = 100)
    private String category;
    @Column(name = "client_id", nullable = false, length = 36)
    private Long clientId;
    @Column(name = "team_id", nullable = false, length = 36)
    private Long teamId;
    @Column(name = "responsable_id", length = 36)
    private String responsableId;
    @Column(name = "sla_expiration", nullable = false)
    private Instant slaExpiration;
    @Column(name = "created_date", nullable = false)
    private Instant createdDate;
    @Column(name = "updated_date", nullable = false)
    private Instant updatedDate;
    @ColumnDefault("(uuid_to_bin(uuid()))")
    @Column(name = "public_id", nullable = false, length = 16)
    private String publicId;
    @Column(name = "public_code", nullable = false, length = 30)
    private String publicCode;

}

