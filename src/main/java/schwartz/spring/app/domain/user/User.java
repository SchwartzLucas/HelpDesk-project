package schwartz.spring.app.domain.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Table(name = "users")
@Entity(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Size(max = 100)
    @NotNull
    @Column(name = "login", nullable = false, length = 100)
    private String login;
    @Size(max = 250)
    @NotNull
    @Column(name = "password", nullable = false, length = 250)
    private String password;
    @Enumerated(EnumType.ORDINAL)
    private UserRole role;
    @NotNull
    @ColumnDefault("1")
    @Column(name = "is_active", nullable = false)
    private Integer isActive;
    @ColumnDefault("(UUID_TO_BIN(UUID()))")
    @Column(name = "client_id", length = 16)
    private UUID clientId;
    @ColumnDefault("(UUID_TO_BIN(UUID()))")
    @Column(name = "team_id", length = 16)
    private UUID teamId;
    @Size(max = 30)
    @Column(name = "public_code", length = 30)
    private String publicCode;
    @JdbcTypeCode(SqlTypes.BINARY)
    @NotNull
    @ColumnDefault("(UUID_TO_BIN(UUID()))")
    @Column(name = "public_id", nullable = false, length = 16)
    private UUID publicId;


    public User(String login, String encryptedPassword, UserRole role, UUID publicId) {
        this.publicId = publicId;
        this.login = login;
        this.password = encryptedPassword;
        this.role = role;
        this.isActive = 1;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role.equals(UserRole.ADMIN_USER)) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_SUPPORT"),
                    new SimpleGrantedAuthority("ROLE_USER"));
        } else if (this.role.equals(UserRole.SUPPORT_USER)) {
            return List.of(new SimpleGrantedAuthority("ROLE_SUPPORT"),
                    new SimpleGrantedAuthority("ROLE_USER"));
        } else {
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
    }

    @Override
    public String getUsername() {
        return this.login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return 1 == this.isActive;
    }
}
