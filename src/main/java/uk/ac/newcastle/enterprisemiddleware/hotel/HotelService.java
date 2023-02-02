package uk.ac.newcastle.enterprisemiddleware.hotel;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>This Service assumes the Control responsibility in the ECB pattern.</p>
 *
 * @author Howie
 * @see HotelValidator
 * @see HotelRepository
 */
@Dependent
public class HotelService {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    HotelValidator validator;

    @Inject
    HotelRepository crud;

    /**
     * <p>Returns a List of all persisted {@link Hotel} objects, sorted alphabetically by Id.<p/>
     *
     * @return List of Hotel objects
     */
    List<Hotel> findAllHotel(){
        return crud.findAllHotel();
    }

    /**
     * <p>Returns a single Hotel object, specified by a String id.<p/>
     *
     * @param id The id field of the Hotel to be returned
     * @return The Hotel with the specified id
     */
    public Hotel findById(Long id) {
        return crud.findById(id);
    }

    /**
     * <p>Returns a single Hotel object, specified by a String phoneNumber.<p/>
     *
     * @param phoneNumber The phoneNumber field of the Hotel to be returned
     * @return The Hotel with the specified phoneNumber
     */
    Hotel findByPhoneNumber(String phoneNumber){
        return crud.findByPhoneNumber(phoneNumber);
    }

    /**
     * <p>Returns a list of Hotel object, specified by a String hotelName.<p/>
     *
     * @param hotelName The hotelName field of the Hotel to be returned
     * @return The Hotels with the specified hotelName
     */
    List<Hotel> findByHotelName(String hotelName){
        return crud.findByHotelName(hotelName);
    }

    /**
     * <p>Returns a single Hotel object, specified by a String postcode.<p/>
     *
     * @param postcode The postcode field of the Hotels to be returned
     * @return The Hotels with the specified postcode
     */
    List<Hotel> findByPostcode(String postcode){
        return crud.findByPostcode(postcode);
    }

    /**
     * <p>Writes the provided Hotel object to the application database.<p/>
     *
     * <p>Validates the data in the provided Hotel object using a {@link HotelValidator} object.<p/>
     *
     * @param hotel The Hotel object to be written to the database using a {@link HotelRepository} object
     * @return The Hotel object that has been successfully written to the application database
     * @throws Exception
     */
    Hotel create(Hotel hotel) throws Exception {
        log.info("HotelService.create() - Creating " + hotel.getHotelName() + " " + hotel.getPhoneNumber());
        validator.validateHotel(hotel);

        // Write the Hotel to the database.
        return crud.create(hotel);
    }

    /**
     * <p>Updates an existing Hotel object in the application database with the provided Hotel object.<p/>
     *
     * <p>Validates the data in the provided Hotel object using a HotelValidator object.<p/>
     *
     * @param hotel The Hotel object to be passed as an update to the application database
     * @return The Hotel object that has been successfully updated in the application database
     * @throws Exception
     */
    Hotel update(Hotel hotel) throws Exception {
        log.info("CustomerService.update() - Updating " + hotel.getHotelName() + " " + hotel.getPhoneNumber());
        validator.validateHotel(hotel);

        return crud.update(hotel);
    }

    /**
     * <p>Deletes the provided Hotel object from the application database if found there.<p/>
     *
     * @param hotel The Hotel object to be removed from the application database
     * @return The Hotel object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    Hotel delete(Hotel hotel) throws Exception {
        log.info("delete() - Deleting " + hotel.toString());

        Hotel deletedHotel = null;

        if (hotel.getId() != null) {
            deletedHotel = crud.delete(hotel);
        } else {
            log.info("delete() - No ID was found so can't Delete.");
        }

        return deletedHotel;
    }
}
