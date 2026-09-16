package schwartz.spring.app.domain.demand;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "demand")
public class Demand {
    @Id
    private Long id;
    @NotNull
    @ToString.Exclude
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @NotNull
    @ToString.Exclude
    @Column(name = "user_demand_id", nullable = false)
    private Long userDemandId;
    @Size(max = 30)
    @ToString.Exclude
    @Column(name = "public_code", length = 30)
    private String publicCode;
    @Size(max = 16)
    @ColumnDefault("(uuid_to_bin(uuid()))")
    @ToString.Exclude
    @Column(name = "public_id", length = 16)
    private UUID publicId;
    @Size(max = 250)
    @NotNull
    @ToString.Exclude
    @Column(name = "title", nullable = false, length = 250)
    private String title;
    @NotNull
    @ToString.Exclude
    @Lob
    @Column(name = "description", nullable = false)
    private String description;
    @Size(max = 150)
    @ToString.Exclude
    @Column(name = "attachments", length = 150)
    private String attachments;

}
