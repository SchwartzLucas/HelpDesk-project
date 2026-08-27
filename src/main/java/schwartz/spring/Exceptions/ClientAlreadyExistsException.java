package schwartz.spring.Exceptions;

public class ClientAlreadyExistsException
        extends RuntimeException {

    public ClientAlreadyExistsException(String email) {
        super("A client with e-mail '" + email + "' already exists");
    }
}