package uk.ac.newcastle.enterprisemiddleware.booking;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>This class provides methods to check Booking objects against arbitrary requirements.</p>
 *
 * @author Howie
 * @see Booking
 * @see BookingRepository
 * @see javax.validation.Validator
 */
@ApplicationScoped
public class BookingValidator {
    @Inject
    Validator validator;

    @Inject
    BookingRepository crud;

    /**
     * <p>Validates the given Booking object and throws validation exceptions based on the type of error. If the error is standard
     * bean validation errors then it will throw a ConstraintValidationException with the set of the constraints violated.<p/>
     *
     * @param booking The Booking object to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     */
    void validateBooking(Booking booking) throws ConstraintViolationException {
        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }
    }

    /**
     * <p>Checks if a Booking with the same hotelId and booking_date is already registered. This is the only way to easily capture the
     * "@UniqueConstraint(columnNames = {"hotelId","booking_date"})" constraint from the Booking class.</p>
     *
     * <p>Since Update will being using hotelId and booking_date that is already in the database we need to make sure that it is the hotelId and booking_date
     * from the record being updated.</p>
     *
     * @param hotelId The hotelId and bookingDate to check is unique
     * @param bookingDate The hotelId and bookingDate to check is unique
     * @param id The booking id to check the email against if it was found
     * @return boolean which represents whether the email was found, and if so if it belongs to the Booking with hotelId and booking_date
     */
    boolean hotelAndDateAlreadyExists(Long hotelId, Date bookingDate, Long id) {
        Booking booking = null;
        Booking bookingWithID = null;
        try {
            booking = crud.findByHotelAndDate(hotelId,bookingDate);
        } catch (NoResultException e) {
            // ignore
        }

        if (booking != null && id != null) {
            try {
                bookingWithID = crud.findById(id);
                if (bookingWithID != null && bookingWithID.getHotelId().equals(hotelId) && bookingWithID.getBookingDate().equals(bookingDate)) {
                    booking = null;
                }
            } catch (NoResultException e) {
                // ignore
            }
        }
        return booking != null;
    }
}
