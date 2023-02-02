package uk.ac.newcastle.enterprisemiddleware.customer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>This Service assumes the Control responsibility in the ECB pattern.</p>
 *
 * @author Howie
 * @see CustomerValidator
 * @see CustomerRepository
 */
@Dependent
public class CustomerService {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    CustomerValidator validator;

    @Inject
    CustomerRepository crud;

    /**
     * <p>Returns a List of all persisted {@link Customer} objects, sorted alphabetically by Id.<p/>
     *
     * @return List of Customer objects
     */
    List<Customer> findAllCustomer(){
        return crud.findAllCustomer();
    }

    /**
     * <p>Returns a single Customer object, specified by a Long id.<p/>
     *
     * @param id The id field of the Customer to be returned
     * @return The Customer with the specified id
     */
    public Customer findById(Long id) {
        return crud.findById(id);
    }

    /**
     * <p>Returns a single Customer object, specified by a String email.</p>
     *
     * <p>If there is more than one Customer with the specified email, only the first encountered will be returned.<p/>
     *
     * @param email The email field of the Customer to be returned
     * @return The first Customer with the specified email
     */
    public Customer findByEmail(String email) {
        return crud.findByEmail(email);
    }

    /**
     * <p>Returns a list of Customer object, specified by a String phoneNumber.</p>
     *
     * <p>If there is more than one Customer with the specified phoneNumber, only the first encountered will be returned.<p/>
     *
     * @param phoneNumber The phoneNumber field of the Customer to be returned
     * @return The first Customer with the specified phoneNumber
     */
    List<Customer> findByPhone(String phoneNumber){
        return crud.findByPhoneNumber(phoneNumber);
    }

    /**
     * <p>Returns a single Customer object, specified by a String firstName.<p/>
     *
     * @param firstName The firstName field of the Customer to be returned
     * @return The first Customer with the specified firstName
     */
    List<Customer> findAllByFirstName(String firstName) {
        return crud.findAllByFirstName(firstName);
    }

    /**
     * <p>Returns a single Customer object, specified by a String lastName.<p/>
     *
     * @param lastName The lastName field of the Customers to be returned
     * @return The Customers with the specified lastName
     */
    List<Customer> findAllByLastName(String lastName) {
        return crud.findAllByLastName(lastName);
    }

    /**
     * <p>Writes the provided Customer object to the application database.<p/>
     *
     * <p>Validates the data in the provided Customer object using a {@link CustomerValidator} object.<p/>
     *
     * @param customer The Customer object to be written to the database using a {@link CustomerRepository} object
     * @return The Customer object that has been successfully written to the application database
     * @throws Exception
     */
    public Customer create(Customer customer) throws Exception {
        log.info("CustomerService.create() - Creating " + customer.getFirstName() + " " + customer.getLastName());
        validator.validateCustomer(customer);
        // Write the customer to the database.
        return crud.create(customer);
    }

    /**
     * <p>Updates an existing Customer object in the application database with the provided Customer object.<p/>
     *
     * <p>Validates the data in the provided Customer object using a CustomerValidator object.<p/>
     *
     * @param customer The Customer object to be passed as an update to the application database
     * @return The Customer object that has been successfully updated in the application database
     * @throws Exception
     */
    Customer update(Customer customer) throws Exception {
        log.info("CustomerService.update() - Updating " + customer.getFirstName() + " " + customer.getLastName());
        validator.validateCustomer(customer);

        return crud.update(customer);
    }

    /**
     * <p>Deletes the provided Customer object from the application database if found there.<p/>
     *
     * @param customer The Customer object to be removed from the application database
     * @return The Customer object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    Customer delete(Customer customer) throws Exception {
        log.info("delete() - Deleting " + customer.toString());

        Customer deletedCustomer = null;

        if (customer.getId() != null) {
            deletedCustomer = crud.delete(customer);
        } else {
            log.info("delete() - No ID was found so can't Delete.");
        }

        return deletedCustomer;
    }
}
