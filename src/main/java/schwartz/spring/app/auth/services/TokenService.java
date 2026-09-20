package schwartz.spring.app.auth.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import schwartz.spring.Utils.TimeConfiguration;
import schwartz.spring.app.domain.user.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TokenService {
    private final TimeConfiguration timeConfiguration;
    @Value("${api.security.token.secret}")
    private String secret;

    public TokenService(TimeConfiguration timeConfiguration) {
        this.timeConfiguration = timeConfiguration;
    }

    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("auth-help-desk")
                    .withSubject(user.getLogin())
                    .withClaim("userId", user.getId()) // ID do usuário como claim customizado
                    .withExpiresAt(generateExpirationData())
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            throw new RuntimeException("Error while generating token: ", e);
        }
    }

    public String validateJWTToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("auth-help-desk")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e) {
            return "";
        }
    }

    private Instant generateExpirationData() {
        return timeConfiguration.clock().instant().plus(2, ChronoUnit.HOURS);
    }
}
