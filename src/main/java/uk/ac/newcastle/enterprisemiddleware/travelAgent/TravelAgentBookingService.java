package uk.ac.newcastle.enterprisemiddleware.travelAgent;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>This Service assumes the Control responsibility in the ECB pattern.</p>
 *
 * @author Howie
 * @see TravelAgentBookingRepository
 */
@Dependent
public class TravelAgentBookingService {
    @Inject
    @Named("logger")
    Logger log;

    @Inject
    TravelAgentBookingRepository crud;

    /**
     * <p>Returns a List of all persisted {@link TravelAgentBooking} objects, sorted alphabetically by id.</p>
     *
     *  @return List of TravelAgentBooking objects
     */
    List<TravelAgentBooking> findAllTravelAgentBooking(){
        return crud.findAllTravelAgentBooking();
    }

    /**
     * <p>Returns a single TravelAgentBooking object, specified by a Long id.<p/>
     *
     * @param id The id field of the TravelAgentBooking to be returned
     * @return The TravelAgentBooking with the specified id
     */
    TravelAgentBooking findById(Long id){
        return crud.findById(id);
    }

    /**
     * <p>Returns a list of TravelAgentBooking object, specified by a Long hotelCustomerId.</p>
     *
     * @param hotelCustomerId The hotelCustomerId field of the TravelAgentBooking to be returned
     * @return The TravelAgentBooking with the specified hotelCustomerId
     */
    List<TravelAgentBooking> findByHotelCustomerId(Long hotelCustomerId){
        return crud.findByHotelCustomerId(hotelCustomerId);
    }

    /**
     * <p>Returns a list of TravelAgentBooking object, specified by a Long flightCustomerId.</p>
     *
     * @param flightCustomerId The flightCustomerId field of the TravelAgentBooking to be returned
     * @return The TravelAgentBooking with the specified flightCustomerId
     */
    List<TravelAgentBooking> findByFlightCustomerId(Long flightCustomerId){
        return crud.findByFlightCustomerId(flightCustomerId);
    }

    /**
     * <p>Returns a list of TravelAgentBooking object, specified by a Long taxiCustomerId.</p>
     *
     * @param taxiCustomerId The taxiCustomerId field of the TravelAgentBooking to be returned
     * @return The TravelAgentBooking with the specified taxiCustomerId
     */
    List<TravelAgentBooking> findByTaxiCustomerId(Long taxiCustomerId){
        return crud.findByTaxiCustomerId(taxiCustomerId);
    }

    /**
     * <p>Writes the provided TravelAgentBooking object to the application database.<p/>
     *
     * @param travelAgentBooking The TravelAgentBooking object to be written to the database using a {@link TravelAgentBookingRepository} object
     * @return The TravelAgentBooking object that has been successfully written to the application database
     * @throws Exception
     */
    TravelAgentBooking create(TravelAgentBooking travelAgentBooking) throws Exception {
        log.info("TravelAgentBookingService.create() - Creating ");

        return crud.create(travelAgentBooking);
    }

    /**
     * <p>Updates an existing TravelAgentBooking object in the application database with the provided TravelAgentBooking object.<p/>
     *
     * @param travelAgentBooking The TravelAgentBooking object to be passed as an update to the application database
     * @return The TravelAgentBooking object that has been successfully updated in the application database
     * @throws Exception
     */
    TravelAgentBooking update(TravelAgentBooking travelAgentBooking) throws Exception {
        log.info("TravelAgentBookingService.update() - Updating ");

        return crud.update(travelAgentBooking);
    }

    /**
     * <p>Deletes the provided TravelAgentBooking object from the application database if found there.<p/>
     *
     * @param travelAgentBooking The TravelAgentBooking object to be removed from the application database
     * @return The TravelAgentBooking object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    TravelAgentBooking delete(TravelAgentBooking travelAgentBooking) throws Exception {
        log.info("delete() - Deleting ");

        TravelAgentBooking deletedTravelAgentBooking = null;

        if (travelAgentBooking.getId() != null) {
            deletedTravelAgentBooking = crud.delete(travelAgentBooking);
        } else {
            log.info("delete() - No ID was found so can't Delete.");
        }

        return deletedTravelAgentBooking;
    }
}
