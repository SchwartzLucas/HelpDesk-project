package schwartz.spring.app.domain.user;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN_USER("admin user", 1),
    SUPPORT_USER("support user", 2),
    COMMON_USER("common user", 3);

    private final String role;
    private final Integer IntegerRole;

    UserRole(String role, Integer integerRole) {
        this.role = role;
        this.IntegerRole = integerRole;
    }

}
