package schwartz.spring.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidDemandException extends Throwable {
    public InvalidDemandException(String message) {
        super(message);
    }
}
