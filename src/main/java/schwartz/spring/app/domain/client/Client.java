package schwartz.spring.app.domain.client;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(
        name = "client",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_client_public_id",
                        columnNames = "public_id"
                ),
                @UniqueConstraint(
                        name = "uk_client_public_code",
                        columnNames = "public_code"
                )
        }
)
@Getter
@Setter
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "public_id",
            nullable = false,
            unique = true,
            columnDefinition = "BINARY(16)"
    )
    @JdbcTypeCode(SqlTypes.BINARY)
    @NotNull
    @ColumnDefault("(UUID_TO_BIN(UUID()))")
    private UUID publicId;

    @Column(
            name = "public_code",
            unique = true,
            length = 30
    )
    @NotNull
    private String publicCode;

    @Column(nullable = false, length = 250)
    @NotNull
    private String name;

    @Column(nullable = false, length = 250)
    private String email;
}
