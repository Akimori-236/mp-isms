package tfip.akimori.server.exceptions;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
