package schwartz.spring.auth.domain.user;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class UserRoleConverter implements AttributeConverter<UserRole, Integer> {

    @Override
    public Integer convertToDatabaseColumn(UserRole role) {
        return role == null ? null : role.getCode();
    }

    @Override
    public UserRole convertToEntityAttribute(Integer code) {
        return code == null ? null : UserRole.fromCode(code);
    }
}
