package schwartz.spring.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MissingAttributeException extends RuntimeException {

    public MissingAttributeException(String attribute) {
        super("The attribute: " + attribute + "is mandatory");
    }

}
