package tfip.akimori.server.exceptions;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnauthorizedException(String string) {
    }
}
