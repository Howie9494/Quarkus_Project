package uk.ac.newcastle.enterprisemiddleware.booking;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>This Service assumes the Control responsibility in the ECB pattern.</p>
 *
 * @author Howie
 * @see BookingValidator
 * @see BookingRepository
 */
@Dependent
public class BookingService {
    @Inject
    @Named("logger")
    Logger log;

    @Inject
    BookingValidator validator;

    @Inject
    BookingRepository crud;

    /**
     * <p>Returns a List of all persisted {@link Booking} objects, sorted alphabetically by Id.<p/>
     *
     * @return List of Booking objects
     */
    List<Booking> findAllBooking(){
        return crud.findAllBooking();
    }

    /**
     * <p>Returns a single Booking object, specified by a String id.<p/>
     *
     * @param id The id field of the Booking to be returned
     * @return The Booking with the specified id
     */
    public Booking findById(Long id) {
        return crud.findById(id);
    }

    /**
     * <p>Returns a list of Booking object, specified by a Long customerId.</p>
     *
     * @param customerId The id field of the Booking to be returned
     * @return The Booking with the specified customerId
     */
    public List<Booking> findByCustomerId(Long customerId){
        return crud.findByCustomerId(customerId);
    }

    /**
     * <p>Returns a list of Booking object, specified by a Long hotelId.</p>
     *
     * @param hotelId The hotelId field of the Booking to be returned
     * @return The Booking with the specified hotelId
     */
    public List<Booking> findByHotelId(Long hotelId){
        return crud.findByHotelId(hotelId);
    }

    /**
     * <p>Returns a list of Booking object, specified by a Long hotelId and Date bookingDate.</p>
     *
     * @param hotelId The hotelId field of the Booking to be returned
     * @param bookingDate The bookingDate field of the Booking to be returned
     * @return The Booking with the specified hotelId and bookingDate
     */
    Booking findByHotelAndDate(Long hotelId, Date bookingDate){
        return crud.findByHotelAndDate(hotelId,bookingDate);
    }

    /**
     * <p>Writes the provided Booking object to the application database.<p/>
     *
     * <p>Validates the data in the provided Booking object using a {@link BookingValidator} object.<p/>
     *
     * @param booking The Booking object to be written to the database using a {@link BookingRepository} object
     * @return The Booking object that has been successfully written to the application database
     * @throws Exception
     */
    public Booking create(Booking booking) throws Exception {
        log.info("BookingService.create() - Creating " + booking.getHotelId() + " _ " + booking.getBookingDate());
        validator.validateBooking(booking);

        return crud.create(booking);
    }


    /**
     * <p>Deletes the provided Booking object from the application database if found there.<p/>
     *
     * @param booking The Booking object to be removed from the application database
     * @return The Booking object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    public Booking delete(Booking booking) throws Exception {
        log.info("delete() - Deleting " + booking.toString());

        Booking deletedBooking = null;

        if (booking.getId() != null) {
            deletedBooking = crud.delete(booking);
        } else {
            log.info("delete() - No ID was found so can't Delete.");
        }

        return deletedBooking;
    }
}
