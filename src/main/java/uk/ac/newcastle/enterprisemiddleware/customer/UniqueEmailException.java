package uk.ac.newcastle.enterprisemiddleware.customer;

import javax.xml.bind.ValidationException;

/**
 * <p>ValidationException caused if a Customer's email address conflicts with that of another Customer.</p>
 *
 * <p>This violates the uniqueness constraint.</p>
 *
 * @author howie
 */
public class UniqueEmailException extends ValidationException {

    public UniqueEmailException(String message) {
        super(message);
    }

    public UniqueEmailException(String message, Throwable cause) {
        super(message, cause);
    }

    public UniqueEmailException(Throwable cause) {
        super(cause);
    }
}
