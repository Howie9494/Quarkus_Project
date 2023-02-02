package uk.ac.newcastle.enterprisemiddleware.booking;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>This is a Repository class and connects the Service/Control layer (see {@link BookingService} with the
 * Domain/Entity Object (see {@link Booking}).<p/>
 *
 * @author Howie
 * @see Booking
 * @see javax.persistence.EntityManager
 */
@Dependent
public class BookingRepository {
    @Inject
    @Named("logger")
    Logger log;

    @Inject
    EntityManager em;

    /**
     * <p>Returns a List of all persisted {@link Booking} objects, sorted alphabetically by id.</p>
     *
     *  @return List of Booking objects
     */
    List<Booking> findAllBooking(){
        log.info("BookingRepository -- findAllBooking method starts execution.");
        TypedQuery<Booking> query = em.createNamedQuery(Booking.FIND_ALL, Booking.class);
        List<Booking> resultList = query.getResultList();
        log.info("BookingRepository -- findAllBooking method completes execution.");
        return resultList;
    }

    /**
     * <p>Returns a single Booking object, specified by a Long id.<p/>
     *
     * @param id The id field of the Booking to be returned
     * @return The Booking with the specified id
     */
    Booking findById(Long id){
        log.info("BookingRepository -- findById method starts execution.");
        Booking booking = em.find(Booking.class, id);
        log.info("BookingRepository -- findById method completes execution.");
        return booking;
    }

    /**
     * <p>Returns a list of Booking object, specified by a Long hotelId and Date bookingDate.</p>
     *
     * @param hotelId The hotelId field of the Booking to be returned
     * @param bookingDate The bookingDate field of the Booking to be returned
     * @return The Booking with the specified hotelId and bookingDate
     */
    Booking findByHotelAndDate(Long hotelId, Date bookingDate){
        log.info("BookingRepository -- findByHotelAndDate method starts execution.");
        TypedQuery<Booking> query = em.createNamedQuery(Booking.FIND_BY_HOTEL_AND_DATE, Booking.class)
                .setParameter("hotelId",hotelId).setParameter("bookingDate",bookingDate);
        Booking resultList = query.getSingleResult();
        log.info("BookingRepository -- findByHotelAndDate method completes execution.");
        return resultList;
    }

    /**
     * <p>Returns a list of Booking object, specified by a Long hotelId.</p>
     *
     * @param hotelId The hotelId field of the Booking to be returned
     * @return The Booking with the specified hotelId
     */
    List<Booking> findByHotelId(Long hotelId){
        log.info("BookingRepository -- findByHotelId method starts execution.");
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Booking> criteria = cb.createQuery(Booking.class);
        Root<Booking> booking = criteria.from(Booking.class);
        criteria.select(booking).where(cb.equal(booking.get("hotelId"), hotelId));
        List<Booking> resultList = em.createQuery(criteria).getResultList();
        log.info("BookingRepository -- findByHotelId method completes execution.");
        return resultList;
    }

    /**
     * <p>Returns a list of Booking object, specified by a Long customerId.</p>
     *
     * @param customerId The customerId field of the Booking to be returned
     * @return The Booking with the specified customerId
     */
    List<Booking> findByCustomerId(Long customerId){
        log.info("BookingRepository -- findByCustomerId method starts execution.");
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Booking> criteria = cb.createQuery(Booking.class);
        Root<Booking> booking = criteria.from(Booking.class);
        criteria.select(booking).where(cb.equal(booking.get("customerId"), customerId));
        List<Booking> resultList = em.createQuery(criteria).getResultList();
        log.info("BookingRepository -- findByCustomerId method completes execution.");
        return resultList;
    }

    /**
     * <p>Persists the provided Booking object to the application database using the EntityManager.</p>
     *
     * @param booking The Booking object to be persisted
     * @return The Booking object that has been persisted
     * @throws Exception
     */
    Booking create(Booking booking) throws Exception {
        log.info("BookingRepository -- create method Creating " + booking.getId());
        em.persist(booking);
        log.info("BookingRepository -- create method completes execution.");
        return booking;
    }

    /**
     * <p>Updates an existing Booking object in the application database with the provided Booking object.</p>
     *
     * @param booking The Booking object to be merged with an existing Booking
     * @return The Booking that has been merged
     * @throws Exception
     */
    Booking update(Booking booking) throws Exception {
        log.info("BookingRepository -- update method Updating " + booking.getId());
        em.merge(booking);
        log.info("BookingRepository -- update method completes execution.");
        return booking;
    }

    /**
     * <p>Deletes the provided Booking object from the application database if found there</p>
     *
     * @param booking The Booking object to be removed from the application database
     * @return The Booking object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    Booking delete(Booking booking) throws Exception {
        log.info("BookingRepository -- delete method Deleting " + booking.getId());
        if (booking.getId() != null) {
            em.remove(em.merge(booking));
        } else {
            log.info("BookingRepository -- delete method - No Id was found so can't Delete.");
        }
        log.info("BookingRepository -- delete method completes execution.");
        return booking;
    }
}
