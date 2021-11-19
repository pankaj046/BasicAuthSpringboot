package sharma.pankaj.auth.exception;

public class CustomExceptionHandler extends RuntimeException {

    public CustomExceptionHandler(String message, Throwable cause) {
        super(message, cause);
    }
    public CustomExceptionHandler(String message) {
        super(message);
    }
}
