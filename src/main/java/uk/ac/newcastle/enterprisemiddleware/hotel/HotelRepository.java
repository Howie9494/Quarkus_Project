package uk.ac.newcastle.enterprisemiddleware.hotel;

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
 * <p>This is a Repository class and connects the Service/Control layer (see {@link HotelService} with the
 * Domain/Entity Object (see {@link Hotel}).<p/>
 *
 * @author Howie
 * @see Hotel
 * @see javax.persistence.EntityManager
 */
@RequestScoped
public class HotelRepository {
    @Inject
    @Named("logger")
    Logger log;

    @Inject
    EntityManager em;

    /**
     * <p>Returns a List of all persisted {@link Hotel} objects, sorted alphabetically by id.</p>
     *
     *  @return List of Hotel objects
     */
    List<Hotel> findAllHotel(){
        log.info("HotelRepository -- findAllHotel method starts execution.");
        TypedQuery<Hotel> query = em.createNamedQuery(Hotel.FIND_ALL, Hotel.class);
        List<Hotel> resultList = query.getResultList();
        log.info("HotelRepository -- findAllHotel method completes execution.");
        return resultList;
    }

    /**
     * <p>Returns a single Hotel object, specified by a Long id.<p/>
     *
     * @param id The id field of the Hotel to be returned
     * @return The Hotel with the specified id
     */
    Hotel findById(Long id){
        log.info("HotelRepository -- findById method starts execution.");
        Hotel hotel = em.find(Hotel.class, id);
        log.info("HotelRepository -- findById method completes execution.");
        return hotel;
    }

    /**
     * <p>Returns a single Hotel object, specified by a String phoneNumber.<p/>
     *
     * @param phoneNumber The phoneNumber field of the Hotel to be returned
     * @return The Hotel with the specified phoneNumber
     */
    Hotel findByPhoneNumber(String phoneNumber){
        log.info("HotelRepository -- findByPhoneNumber method starts execution.");
        TypedQuery<Hotel> query = em.createNamedQuery(Hotel.FIND_BY_PHONE_NUMBER,Hotel.class).setParameter("phoneNumber",phoneNumber);
        Hotel singleResult = query.getSingleResult();
        log.info("HotelRepository -- findByPhoneNumber method completes execution.");
        return singleResult;
    }

    /**
     * <p>Returns a list of Hotel object, specified by a String hotelName.</p>
     *
     * @param hotelName The hotelName field of the Hotel to be returned
     * @return The Hotel with the specified hotelName
     */
    List<Hotel> findByHotelName(String hotelName){
        log.info("HotelRepository -- findByHotelName method starts execution.");
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Hotel> criteria = cb.createQuery(Hotel.class);
        Root<Hotel> hotel = criteria.from(Hotel.class);
        criteria.select(hotel).where(cb.equal(hotel.get("hotelName"), hotelName));
        List<Hotel> resultList = em.createQuery(criteria).getResultList();
        log.info("HotelRepository -- findByHotelName method completes execution.");
        return resultList;
    }

    /**
     * <p>Returns a single Hotel object, specified by a String postcode.<p/>
     *
     * @param postcode The postcode field of the Hotels to be returned
     * @return The Hotels with the specified postcode
     */
    List<Hotel> findByPostcode(String postcode){
        log.info("HotelRepository -- findByPostcode method starts execution.");
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Hotel> criteria = cb.createQuery(Hotel.class);
        Root<Hotel> hotel = criteria.from(Hotel.class);
        criteria.select(hotel).where(cb.equal(hotel.get("postcode"), postcode));
        List<Hotel> resultList = em.createQuery(criteria).getResultList();
        log.info("HotelRepository -- findByPostcode method completes execution.");
        return resultList;
    }

    /**
     * <p>Persists the provided Hotel object to the application database using the EntityManager.</p>
     *
     * @param hotel The Hotel object to be persisted
     * @return The Hotel object that has been persisted
     * @throws Exception
     */
    Hotel create(Hotel hotel) throws Exception {
        log.info("HotelRepository -- create method Creating " + hotel.getHotelName() + " " + hotel.getPhoneNumber());
        em.persist(hotel);
        log.info("HotelRepository -- create method completes execution.");
        return hotel;
    }

    /**
     * <p>Updates an existing Hotel object in the application database with the provided Hotel object.</p>
     *
     * @param hotel The Hotel object to be merged with an existing Hotel
     * @return The Hotel that has been merged
     * @throws Exception
     */
    Hotel update(Hotel hotel) throws Exception {
        log.info("HotelRepository -- update method Updating " + hotel.getHotelName() + " " + hotel.getPhoneNumber());
        em.merge(hotel);
        log.info("HotelRepository -- update method completes execution.");
        return hotel;
    }

    /**
     * <p>Deletes the provided Hotel object from the application database if found there</p>
     *
     * @param hotel The Hotel object to be removed from the application database
     * @return The Hotel object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    Hotel delete(Hotel hotel) throws Exception {
        log.info("HotelRepository -- delete method Deleting " + hotel.getHotelName() + " " + hotel.getPhoneNumber());
        if (hotel.getId() != null) {
            em.remove(em.merge(hotel));
        } else {
            log.info("HotelRepository -- delete method - No Id was found so can't Delete.");
        }
        log.info("HotelRepository -- delete method completes execution.");
        return hotel;
    }

}
