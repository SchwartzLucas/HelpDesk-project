package schwartz.spring.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ClientAlreadyExistsException
        extends RuntimeException {

    public ClientAlreadyExistsException(String email) {
        super("A client with e-mail '" + email + "' already exists");
    }
}