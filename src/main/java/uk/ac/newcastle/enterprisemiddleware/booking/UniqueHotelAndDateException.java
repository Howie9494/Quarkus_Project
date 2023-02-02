package uk.ac.newcastle.enterprisemiddleware.booking;

import javax.validation.ValidationException;

/**
 * <p>ValidationException caused if a Booking's hotel and date conflicts with that of another Booking.</p>
 *
 * <p>This violates the uniqueness constraint.</p>
 *
 * @author howie
 */
public class UniqueHotelAndDateException extends ValidationException {
    public UniqueHotelAndDateException(String message) {
        super(message);
    }

    public UniqueHotelAndDateException(String message, Throwable cause) {
        super(message, cause);
    }

    public UniqueHotelAndDateException(Throwable cause) {
        super(cause);
    }
}
