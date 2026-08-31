package schwartz.spring.Exceptions;

public class MissingAttributeException extends RuntimeException {

    public MissingAttributeException(String attribute) {
        super("The attribute: " + attribute + "is mandatory");
    }

}
