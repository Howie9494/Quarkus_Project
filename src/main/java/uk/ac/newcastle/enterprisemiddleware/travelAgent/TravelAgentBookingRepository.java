package uk.ac.newcastle.enterprisemiddleware.travelAgent;

import uk.ac.newcastle.enterprisemiddleware.hotel.Hotel;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>This is a Repository class and connects the Service/Control layer (see {@link TravelAgentBookingService} with the
 * Domain/Entity Object (see {@link TravelAgentBooking}).<p/>
 *
 * @author Howie
 * @see TravelAgentBooking
 * @see javax.persistence.EntityManager
 */
@RequestScoped
public class TravelAgentBookingRepository {
    @Inject
    @Named("logger")
    Logger log;

    @Inject
    EntityManager em;

    /**
     * <p>Returns a List of all persisted {@link TravelAgentBooking} objects, sorted alphabetically by id.</p>
     *
     *  @return List of Hotel objects
     */
    List<TravelAgentBooking> findAllTravelAgentBooking(){
        log.info("TravelAgentBookingRepository -- findAllTravelAgentBooking method starts execution.");
        TypedQuery<TravelAgentBooking> query = em.createNamedQuery(TravelAgentBooking.FIND_ALL, TravelAgentBooking.class);
        List<TravelAgentBooking> resultList = query.getResultList();
        log.info("TravelAgentBookingRepository -- findAllTravelAgentBooking method completes execution.");
        return resultList;
    }

    /**
     * <p>Returns a single TravelAgentBooking object, specified by a Long id.<p/>
     *
     * @param id The id field of the TravelAgentBooking to be returned
     * @return The TravelAgentBooking with the specified id
     */
    TravelAgentBooking findById(Long id){
        log.info("TravelAgentBookingRepository -- findById method starts execution.");
        TravelAgentBooking travelAgentBooking = em.find(TravelAgentBooking.class, id);
        log.info("TravelAgentBookingRepository -- findById method completes execution.");
        return travelAgentBooking;
    }

    /**
     * <p>Returns a list of TravelAgentBooking object, specified by a Long hotelCustomerId.</p>
     *
     * @param hotelCustomerId The hotelCustomerId field of the TravelAgentBooking to be returned
     * @return The TravelAgentBooking with the specified hotelCustomerId
     */
    List<TravelAgentBooking> findByHotelCustomerId(Long hotelCustomerId){
        log.info("TravelAgentBookingRepository -- findByHotelCustomerId method starts execution.");
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TravelAgentBooking> criteria = cb.createQuery(TravelAgentBooking.class);
        Root<TravelAgentBooking> travelAgentBooking = criteria.from(TravelAgentBooking.class);
        criteria.select(travelAgentBooking).where(cb.equal(travelAgentBooking.get("hotelCustomerId"), hotelCustomerId));
        List<TravelAgentBooking> resultList = em.createQuery(criteria).getResultList();
        log.info("TravelAgentBookingRepository -- findByHotelCustomerId method completes execution.");
        return resultList;
    }

    /**
     * <p>Returns a list of TravelAgentBooking object, specified by a Long flightCustomerId.</p>
     *
     * @param flightCustomerId The flightCustomerId field of the TravelAgentBooking to be returned
     * @return The TravelAgentBooking with the specified flightCustomerId
     */
    List<TravelAgentBooking> findByFlightCustomerId(Long flightCustomerId){
        log.info("TravelAgentBookingRepository -- findByFlightCustomerId method starts execution.");
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TravelAgentBooking> criteria = cb.createQuery(TravelAgentBooking.class);
        Root<TravelAgentBooking> travelAgentBooking = criteria.from(TravelAgentBooking.class);
        criteria.select(travelAgentBooking).where(cb.equal(travelAgentBooking.get("flightCustomerId"), flightCustomerId));
        List<TravelAgentBooking> resultList = em.createQuery(criteria).getResultList();
        log.info("TravelAgentBookingRepository -- findByFlightCustomerId method completes execution.");
        return resultList;
    }

    /**
     * <p>Returns a list of TravelAgentBooking object, specified by a Long taxiCustomerId.</p>
     *
     * @param taxiCustomerId The taxiCustomerId field of the TravelAgentBooking to be returned
     * @return The TravelAgentBooking with the specified taxiCustomerId
     */
    List<TravelAgentBooking> findByTaxiCustomerId(Long taxiCustomerId){
        log.info("TravelAgentBookingRepository -- findByTaxiCustomerId method starts execution.");
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TravelAgentBooking> criteria = cb.createQuery(TravelAgentBooking.class);
        Root<TravelAgentBooking> travelAgentBooking = criteria.from(TravelAgentBooking.class);
        criteria.select(travelAgentBooking).where(cb.equal(travelAgentBooking.get("taxiCustomerId"), taxiCustomerId));
        List<TravelAgentBooking> resultList = em.createQuery(criteria).getResultList();
        log.info("TravelAgentBookingRepository -- findByTaxiCustomerId method completes execution.");
        return resultList;
    }

    /**
     * <p>Persists the provided TravelAgentBooking object to the application database using the EntityManager.</p>
     *
     * @param travelAgentBooking The TravelAgentBooking object to be persisted
     * @return The TravelAgentBooking object that has been persisted
     * @throws Exception
     */
    TravelAgentBooking create(TravelAgentBooking travelAgentBooking) throws Exception {
        log.info("TravelAgentBookingRepository -- create method Creating");
        em.persist(travelAgentBooking);
        log.info("TravelAgentBookingRepository -- create method completes execution.");
        return travelAgentBooking;
    }

    /**
     * <p>Updates an existing TravelAgentBooking object in the application database with the provided TravelAgentBooking object.</p>
     *
     * @param travelAgentBooking The TravelAgentBooking object to be merged with an existing TravelAgentBooking
     * @return The TravelAgentBooking that has been merged
     * @throws Exception
     */
    TravelAgentBooking update(TravelAgentBooking travelAgentBooking) throws Exception {
        log.info("TravelAgentBookingRepository -- update method Updating ");
        em.merge(travelAgentBooking);
        log.info("TravelAgentBookingRepository -- update method completes execution.");
        return travelAgentBooking;
    }

    /**
     * <p>Deletes the provided TravelAgentBooking object from the application database if found there</p>
     *
     * @param travelAgentBooking The TravelAgentBooking object to be removed from the application database
     * @return The TravelAgentBooking object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    TravelAgentBooking delete(TravelAgentBooking travelAgentBooking) throws Exception {
        log.info("TravelAgentBookingRepository -- delete method Deleting ");
        if (travelAgentBooking.getId() != null) {
            em.remove(em.merge(travelAgentBooking));
        } else {
            log.info("TravelAgentBookingRepository -- delete method - No Id was found so can't Delete.");
        }
        log.info("TravelAgentBookingRepository -- delete method completes execution.");
        return travelAgentBooking;
    }
}
