package schwartz.spring.app.domain.client;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "client")
@Entity(name = "client")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Client {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false, length = 250)
    private String name;
    @Column(name = "email", nullable = false, length = 250)
    private String email;

    public Client(String name, String email) {
        this.email = email;
        this.name = name;
    }
}
