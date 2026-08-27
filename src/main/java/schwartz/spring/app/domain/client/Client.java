package schwartz.spring.app.domain.client;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    private UUID publicId;

    @Column(
            name = "public_code",
            unique = true,
            length = 30
    )
    private String publicCode;

    @Column(nullable = false, length = 250)
    private String name;

    @Column(nullable = false, length = 250)
    private String email;
}
