package schwartz.spring.infraStructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class PasswordEncoderTest {

    @Test
    public void testPasswordEncoding() {
        String rawPassword = "L400250";
        String encodedPassword = this.passwordEncoder().encode(rawPassword);

        System.out.println("Encoded Password: " + encodedPassword);

        // Verify that the password is encoded correctly
        boolean matches = this.passwordEncoder().matches(rawPassword, encodedPassword);
        assert matches : "Password does not match the encoded password";
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}