package schwartz.spring.app.domain.team;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Table(name = "team")
@Entity(name = "team")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "name", nullable = false, length = 250)
    private String name;
    @Column(name = "description", nullable = false, length = 250)
    private String description;
    @Column(name = "manager_id", nullable = false, length = 36)
    private String managerId;
    @JdbcTypeCode(SqlTypes.BINARY)
    @NotNull
    @ColumnDefault("(UUID_TO_BIN(UUID()))")
    @Column(name = "public_id", nullable = false, length = 16)
    private UUID publicId;
    @Column(name = "public_code", length = 30)
    private String publicCode;

}
