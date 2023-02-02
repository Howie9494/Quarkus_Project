package uk.ac.newcastle.enterprisemiddleware.customer;

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
 * <p>This is a Repository class and connects the Service/Control layer (see {@link CustomerService} with the
 * Domain/Entity Object (see {@link Customer}).<p/>
 *
 * @author Howie
 * @see Customer
 * @see javax.persistence.EntityManager
 */
@RequestScoped
public class CustomerRepository {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    EntityManager em;

    /**
     * <p>Returns a List of all persisted {@link Customer} objects, sorted alphabetically by id.</p>
     *
     *  @return List of Customer objects
     */
    List<Customer> findAllCustomer(){
        log.info("CustomerRepository -- findAllCustomer method starts execution.");
        TypedQuery<Customer> query = em.createNamedQuery(Customer.FIND_ALL, Customer.class);
        List<Customer> resultList = query.getResultList();
        log.info("CustomerRepository -- findAllCustomer method completes execution.");
        return resultList;
    }

    /**
     * <p>Returns a single Customer object, specified by a Long id.<p/>
     *
     * @param id The id field of the Customer to be returned
     * @return The Customer with the specified id
     */
    Customer findById(Long id){
        log.info("CustomerRepository -- findByUserName method starts execution.");
        Customer customer = em.find(Customer.class, id);
        log.info("CustomerRepository -- findByUserName method completes execution.");
        return customer;
    }

    /**
     * <p>Returns a single Customer object, specified by a String email.</p>
     *
     * @param email The email field of the Customer to be returned
     * @return The Customer with the specified email
     */
    Customer findByEmail(String email) {
        log.info("CustomerRepository -- findByEmail method starts execution.");
        TypedQuery<Customer> query = em.createNamedQuery(Customer.FIND_BY_EMAIL, Customer.class).setParameter("email", email);
        Customer singleResult = query.getSingleResult();
        log.info("CustomerRepository -- findByEmail method completes execution.");
        return singleResult;
    }

    /**
     * <p>Returns a list of Customer object, specified by a String phoneNumber.</p>
     *
     * @param phoneNumber The email field of the Customer to be returned
     * @return The Customer with the specified phoneNumber
     */
    List<Customer> findByPhoneNumber(String phoneNumber) {
        log.info("CustomerRepository -- findByPhoneNumber method starts execution.");
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Customer> criteria = cb.createQuery(Customer.class);
        Root<Customer> customer = criteria.from(Customer.class);
        criteria.select(customer).where(cb.equal(customer.get("phoneNumber"), phoneNumber));
        List<Customer> resultList = em.createQuery(criteria).getResultList();
        log.info("CustomerRepository -- findByPhoneNumber method completes execution.");
        return resultList;
    }

    /**
     * <p>Returns a list of Customer objects, specified by a String firstName.<p/>
     *
     * @param firstName The firstName field of the Customers to be returned
     * @return The Customers with the specified firstName
     */
    List<Customer> findAllByFirstName(String firstName) {
        log.info("CustomerRepository -- findAllByFirstName method starts execution.");
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Customer> criteria = cb.createQuery(Customer.class);
        Root<Customer> customer = criteria.from(Customer.class);
        criteria.select(customer).where(cb.equal(customer.get("firstName"), firstName));
        List<Customer> resultList = em.createQuery(criteria).getResultList();
        log.info("CustomerRepository -- findByPhoneNumber method completes execution.");
        return resultList;
    }

    /**
     * <p>Returns a single Customer object, specified by a String lastName.<p/>
     *
     * @param lastName The lastName field of the Customers to be returned
     * @return The Customers with the specified lastName
     */
    List<Customer> findAllByLastName(String lastName) {
        log.info("CustomerRepository -- findAllByLastName method starts execution.");
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Customer> criteria = cb.createQuery(Customer.class);
        Root<Customer> customer = criteria.from(Customer.class);
        criteria.select(customer).where(cb.equal(customer.get("lastName"), lastName));
        List<Customer> resultList = em.createQuery(criteria).getResultList();
        log.info("CustomerRepository -- findAllByLastName method completes execution.");
        return resultList;
    }

    /**
     * <p>Persists the provided Customer object to the application database using the EntityManager.</p>
     *
     * @param customer The Customer object to be persisted
     * @return The Customer object that has been persisted
     * @throws Exception
     */
    Customer create(Customer customer) throws Exception {
        log.info("CustomerRepository -- create method Creating " + customer.getFirstName() + " " + customer.getLastName());
        em.persist(customer);
        log.info("CustomerRepository -- create method completes execution.");
        return customer;
    }

    /**
     * <p>Updates an existing Customer object in the application database with the provided Customer object.</p>
     *
     * @param customer The Customer object to be merged with an existing Customer
     * @return The Customer that has been merged
     * @throws Exception
     */
    Customer update(Customer customer) throws Exception {
        log.info("CustomerRepository -- update method Updating " + customer.getFirstName() + " " + customer.getLastName());
        em.merge(customer);
        log.info("CustomerRepository -- update method completes execution.");
        return customer;
    }

    /**
     * <p>Deletes the provided Customer object from the application database if found there</p>
     *
     * @param customer The Customer object to be removed from the application database
     * @return The Customer object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    Customer delete(Customer customer) throws Exception {
        log.info("CustomerRepository -- delete method Deleting " + customer.getFirstName() + " " + customer.getLastName());
        if (customer.getId() != null) {
            em.remove(em.merge(customer));
        } else {
            log.info("CustomerRepository -- delete method - No Id was found so can't Delete.");
        }
        log.info("CustomerRepository -- delete method completes execution.");
        return customer;
    }

}
