package nl.rabobank.exceptions;


/**
 * Thrown when an error relating to the account holder occurs
 */
public class AccountHolderException extends Exception {

    public AccountHolderException() {
    }

    public AccountHolderException(String message) {
        super(message);
    }

    public AccountHolderException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountHolderException(Throwable cause) {
        super(cause);
    }

    public AccountHolderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
