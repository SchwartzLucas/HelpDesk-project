package schwartz.spring.auth.domain.user;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN_USER("admin user", 1),
    SUPPORT_USER("support user", 2),
    COMMON_USER("common user", 3);

    private final String label;
    private final int code;

    UserRole(String label, int code) {
        this.label = label;
        this.code = code;
    }

    public static UserRole fromCode(int code) {
        return switch (code) {
            case 1 -> ADMIN_USER;
            case 2 -> SUPPORT_USER;
            case 3 -> COMMON_USER;
            default -> COMMON_USER;
        };
    }
}
