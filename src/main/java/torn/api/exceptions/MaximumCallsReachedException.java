package torn.api.exceptions;

public class MaximumCallsReachedException extends Exception {
    public MaximumCallsReachedException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public MaximumCallsReachedException(String errorMessage) {
        super(errorMessage);
    }
}
