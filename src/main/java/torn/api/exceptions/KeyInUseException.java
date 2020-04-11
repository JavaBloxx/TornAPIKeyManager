package torn.api.exceptions;

public class KeyInUseException extends Exception {
    public KeyInUseException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public KeyInUseException(String errorMessage) {
        super(errorMessage);
    }
}
