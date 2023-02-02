package uk.ac.newcastle.enterprisemiddleware.contact;


import org.eclipse.microprofile.rest.client.inject.RestClient;
import uk.ac.newcastle.enterprisemiddleware.area.Area;
import uk.ac.newcastle.enterprisemiddleware.area.AreaService;
import uk.ac.newcastle.enterprisemiddleware.area.InvalidAreaCodeException;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>This Service assumes the Control responsibility in the ECB pattern.</p>
 *
 * <p>The validation is done here so that it may be used by other Boundary Resources. Other Business Logic would go here
 * as well.</p>
 *
 * <p>There are no access modifiers on the methods, making them 'package' scope.  They should only be accessed by a
 * Boundary / Web Service class with public methods.</p>
 *
 *
 * @author Joshua Wilson
 * @see ContactValidator
 * @see ContactRepository
 */
@Dependent
public class ContactService {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    ContactValidator validator;

    @Inject
    ContactRepository crud;

    //Removed temporarily due to non-existing AreaService
    @RestClient
    AreaService areaService;

    /**
     * <p>Returns a List of all persisted {@link Contact} objects, sorted alphabetically by last name.<p/>
     *
     * @return List of Contact objects
     */
    List<Contact> findAllOrderedByName() {
        return crud.findAllOrderedByName();
    }

    /**
     * <p>Returns a single Contact object, specified by a Long id.<p/>
     *
     * @param id The id field of the Contact to be returned
     * @return The Contact with the specified id
     */
    Contact findById(Long id) {
        return crud.findById(id);
    }

    /**
     * <p>Returns a single Contact object, specified by a String email.</p>
     *
     * <p>If there is more than one Contact with the specified email, only the first encountered will be returned.<p/>
     *
     * @param email The email field of the Contact to be returned
     * @return The first Contact with the specified email
     */
    Contact findByEmail(String email) {
        return crud.findByEmail(email);
    }

    /**
     * <p>Returns a single Contact object, specified by a String firstName.<p/>
     *
     * @param firstName The firstName field of the Contact to be returned
     * @return The first Contact with the specified firstName
     */
    List<Contact> findAllByFirstName(String firstName) {
        return crud.findAllByFirstName(firstName);
    }

    /**
     * <p>Returns a single Contact object, specified by a String lastName.<p/>
     *
     * @param lastName The lastName field of the Contacts to be returned
     * @return The Contacts with the specified lastName
     */
    List<Contact> findAllByLastName(String lastName) {
        return crud.findAllByLastName(lastName);
    }

    /**
     * <p>Writes the provided Contact object to the application database.<p/>
     *
     * <p>Validates the data in the provided Contact object using a {@link ContactValidator} object.<p/>
     *
     * @param contact The Contact object to be written to the database using a {@link ContactRepository} object
     * @return The Contact object that has been successfully written to the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    Contact create(Contact contact) throws Exception {
        log.info("ContactService.create() - Creating " + contact.getFirstName() + " " + contact.getLastName());

        // Check to make sure the data fits with the parameters in the Contact model and passes validation.
        validator.validateContact(contact);



        //Create client service instance to make REST requests to upstream service
        try {
            //Removed temporarily due to non-existing AreaService
             Area area = areaService.getAreaById(Integer.parseInt(contact.getPhoneNumber().substring(1, 4)));
             contact.setState(area.getState());
        } catch (ClientErrorException e) {
            if (e.getResponse().getStatusInfo() == Response.Status.NOT_FOUND) {
                throw new InvalidAreaCodeException("The area code provided does not exist", e);
            } else {
                throw e;
            }
        }

        // Write the contact to the database.
        return crud.create(contact);
    }

    /**
     * <p>Updates an existing Contact object in the application database with the provided Contact object.<p/>
     *
     * <p>Validates the data in the provided Contact object using a ContactValidator object.<p/>
     *
     * @param contact The Contact object to be passed as an update to the application database
     * @return The Contact object that has been successfully updated in the application database
     * @throws ConstraintViolationException, ValidationException, Exception
     */
    Contact update(Contact contact) throws Exception {
        log.info("ContactService.update() - Updating " + contact.getFirstName() + " " + contact.getLastName());

        // Check to make sure the data fits with the parameters in the Contact model and passes validation.
        validator.validateContact(contact);

        try {
            //Removed temporarily due to non-existing AreaService
            Area area = areaService.getAreaById(Integer.parseInt(contact.getPhoneNumber().substring(1, 4)));
            contact.setState(area.getState());
        } catch (ClientErrorException e) {
            if (e.getResponse().getStatusInfo() == Response.Status.NOT_FOUND) {
                throw new InvalidAreaCodeException("The area code provided does not exist", e);
            } else {
                throw e;
            }
        }

        // Either update the contact or add it if it can't be found.
        return crud.update(contact);
    }

    /**
     * <p>Deletes the provided Contact object from the application database if found there.<p/>
     *
     * @param contact The Contact object to be removed from the application database
     * @return The Contact object that has been successfully removed from the application database; or null
     * @throws Exception
     */
    Contact delete(Contact contact) throws Exception {
        log.info("delete() - Deleting " + contact.toString());

        Contact deletedContact = null;

        if (contact.getId() != null) {
            deletedContact = crud.delete(contact);
        } else {
            log.info("delete() - No ID was found so can't Delete.");
        }

        return deletedContact;
    }
}
