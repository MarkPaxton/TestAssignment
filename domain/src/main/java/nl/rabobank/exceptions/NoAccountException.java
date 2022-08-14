package nl.rabobank.exceptions;

/**
 * Thrown when a requested account does not exist
 */

public class NoAccountException extends Exception {
    public NoAccountException() {
    }

    public NoAccountException(String message) {
        super(message);
    }

    public NoAccountException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoAccountException(Throwable cause) {
        super(cause);
    }

    public NoAccountException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
