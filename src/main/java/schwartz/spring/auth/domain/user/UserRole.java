package schwartz.spring.auth.domain.user;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN_USER("admin user", 1),
    SUPPORT_USER("support user", 2),
    COMMON_USER("common user", 3);

    private final String role;

    UserRole(String role, int i) {
        this.role = role;
    }

}
