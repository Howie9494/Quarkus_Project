package uk.ac.newcastle.enterprisemiddleware.hotel;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>This class provides methods to check Hotel objects against arbitrary requirements.</p>
 *
 * @author Howie
 * @see Hotel
 * @see HotelRepository
 * @see javax.validation.Validator
 */
@ApplicationScoped
public class HotelValidator {
    @Inject
    Validator validator;

    @Inject
    HotelRepository crud;

    /**
     * <p>Validates the given Hotel object and throws validation exceptions based on the type of error. If the error is standard
     * bean validation errors then it will throw a ConstraintValidationException with the set of the constraints violated.<p/>
     *
     * @param hotel The Hotel object to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws UniquePhoneNumberException If Hotel with the same phoneNumber already exists
     */
    void validateHotel(Hotel hotel) throws ConstraintViolationException, UniquePhoneNumberException {
        Set<ConstraintViolation<Hotel>> violations = validator.validate(hotel);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }

        if (phoneNumberAlreadyExists(hotel.getPhoneNumber(), hotel.getId())) {
            throw new UniquePhoneNumberException("Unique phoneNumber Violation");
        }
    }

    /**
     * <p>Checks if a Hotel with the same phoneNumber address is already registered. This is the only way to easily capture the
     * "@UniqueConstraint(columnNames = "phoneNumber")" constraint from the Hotel class.</p>
     *
     * <p>Since Update will being using an phoneNumber that is already in the database we need to make sure that it is the phoneNumber
     * from the record being updated.</p>
     *
     * @param phoneNumber The phoneNumber to check is unique
     * @param id The hotel id to check the phoneNumber against if it was found
     * @return boolean which represents whether the phoneNumber was found, and if so if it belongs to the hotel with id
     */
    boolean phoneNumberAlreadyExists(String phoneNumber, Long id) {
        Hotel hotel = null;
        Hotel hotelWithID = null;
        try {
            hotel = crud.findByPhoneNumber(phoneNumber);
        } catch (NoResultException e) {
            // ignore
        }

        if (hotel != null && id != null) {
            try {
                hotelWithID = crud.findById(id);
                if (hotelWithID != null && hotelWithID.getPhoneNumber().equals(phoneNumber)) {
                    hotel = null;
                }
            } catch (NoResultException e) {
                // ignore
            }
        }
        return hotel != null;
    }
}
