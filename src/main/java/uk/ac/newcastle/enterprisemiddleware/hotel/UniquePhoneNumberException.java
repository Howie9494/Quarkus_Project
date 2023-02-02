package uk.ac.newcastle.enterprisemiddleware.hotel;

import javax.xml.bind.ValidationException;

/**
 * <p>ValidationException caused if a Hotel's phoneNumber conflicts with that of another Hotel.</p>
 *
 * <p>This violates the uniqueness constraint.</p>
 *
 * @author howie
 */
public class UniquePhoneNumberException extends ValidationException {

    public UniquePhoneNumberException(String message) {
        super(message);
    }

    public UniquePhoneNumberException(String message, Throwable cause) {
        super(message, cause);
    }

    public UniquePhoneNumberException(Throwable cause) {
        super(cause);
    }
}
