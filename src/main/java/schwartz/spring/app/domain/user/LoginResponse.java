package schwartz.spring.app.domain.user;

public record LoginResponse(
        String token
) {
    public static LoginResponse from(String token){
        return new LoginResponse(
                token
        );
    }
}
