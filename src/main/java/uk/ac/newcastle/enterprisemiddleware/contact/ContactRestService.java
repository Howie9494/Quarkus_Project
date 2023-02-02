package uk.ac.newcastle.enterprisemiddleware.contact;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.jboss.resteasy.reactive.Cache;
import uk.ac.newcastle.enterprisemiddleware.area.InvalidAreaCodeException;
import uk.ac.newcastle.enterprisemiddleware.util.RestServiceException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


/**
 * <p>This class produces a RESTful service exposing the functionality of {@link ContactService}.</p>
 *
 * <p>The Path annotation defines this as a REST Web Service using JAX-RS.</p>
 *
 * <p>By placing the Consumes and Produces annotations at the class level the methods all default to JSON.  However, they
 * can be overriden by adding the Consumes or Produces annotations to the individual methods.</p>
 *
 * <p>It is Stateless to "inform the container that this RESTful web service should also be treated as an EJB and allow
 * transaction demarcation when accessing the database." - Antonio Goncalves</p>
 *
 * <p>The full path for accessing endpoints defined herein is: api/contacts/*</p>
 *
 * @author Joshua Wilson
 * @see ContactService
 * @see javax.ws.rs.core.Response
 */
@Path("/contacts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ContactRestService {
    @Inject
    @Named("logger")
    Logger log;

    @Inject
    ContactService service;

    /**
     * <p>Return all the Contacts.  They are sorted alphabetically by name.</p>
     *
     * <p>The url may optionally include query parameters specifying a Contact's name</p>
     *
     * <p>Examples: <pre>GET api/contacts?firstname=John</pre>, <pre>GET api/contacts?firstname=John&lastname=Smith</pre></p>
     *
     * @return A Response containing a list of Contacts
     */
    @GET
    @Operation(summary = "Fetch all Contacts", description = "Returns a JSON array of all stored Contact objects.")
    public Response retrieveAllContacts(@QueryParam("firstname") String firstname, @QueryParam("lastname") String lastname) {
        //Create an empty collection to contain the intersection of Contacts to be returned
        List<Contact> contacts;

        if(firstname == null && lastname == null) {
            contacts = service.findAllOrderedByName();
        } else if(lastname == null) {
            contacts = service.findAllByFirstName(firstname);
        } else if(firstname == null) {
            contacts = service.findAllByLastName(lastname);
        } else {
            contacts = service.findAllByFirstName(firstname);
            contacts.retainAll(service.findAllByLastName(lastname));
        }

        return Response.ok(contacts).build();
    }

    /**
     * <p>Search for and return a Contact identified by email address.<p/>
     *
     * <p>Path annotation includes very simple regex to differentiate between email addresses and Ids.
     * <strong>DO NOT</strong> attempt to use this regex to validate email addresses.</p>
     *
     *
     * @param email The string parameter value provided as a Contact's email
     * @return A Response containing a single Contact
     */
    @GET
    @Cache
    @Path("/email/{email:.+[%40|@].+}")
    @Operation(
            summary = "Fetch a Contact by Email",
            description = "Returns a JSON representation of the Contact object with the provided email."
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description ="Contact found"),
            @APIResponse(responseCode = "404", description = "Contact with email not found")
    })
    public Response retrieveContactsByEmail(
            @Parameter(description = "Email of Contact to be fetched", required = true)
            @PathParam("email")
            String email) {

        Contact contact;
        try {
            contact = service.findByEmail(email);
        } catch (NoResultException e) {
            // Verify that the contact exists. Return 404, if not present.
            throw new RestServiceException("No Contact with the email " + email + " was found!", Response.Status.NOT_FOUND);
        }
        return Response.ok(contact).build();
    }

    /**
     * <p>Search for and return a Contact identified by id.</p>
     *
     * @param id The long parameter value provided as a Contact's id
     * @return A Response containing a single Contact
     */
    @GET
    @Cache
    @Path("/{id:[0-9]+}")
    @Operation(
            summary = "Fetch a Contact by id",
            description = "Returns a JSON representation of the Contact object with the provided id."
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description ="Contact found"),
            @APIResponse(responseCode = "404", description = "Contact with id not found")
    })
    public Response retrieveContactById(
            @Parameter(description = "Id of Contact to be fetched")
            @Schema(minimum = "0", required = true)
            @PathParam("id")
            long id) {

        Contact contact = service.findById(id);
        if (contact == null) {
            // Verify that the contact exists. Return 404, if not present.
            throw new RestServiceException("No Contact with the id " + id + " was found!", Response.Status.NOT_FOUND);
        }
        log.info("findById " + id + ": found Contact = " + contact);

        return Response.ok(contact).build();
    }

    /**
     * <p>Creates a new contact from the values provided. Performs validation and will return a JAX-RS response with
     * either 201 (Resource created) or with a map of fields, and related errors.</p>
     *
     * @param contact The Contact object, constructed automatically from JSON input, to be <i>created</i> via
     * {@link ContactService#create(Contact)}
     * @return A Response indicating the outcome of the create operation
     */
    @SuppressWarnings("unused")
    @POST
    @Operation(description = "Add a new Contact to the database")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Contact created successfully."),
            @APIResponse(responseCode = "400", description = "Invalid Contact supplied in request body"),
            @APIResponse(responseCode = "409", description = "Contact supplied in request body conflicts with an existing Contact"),
            @APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request")
    })
    @Transactional
    public Response createContact(
            @Parameter(description = "JSON representation of Contact object to be added to the database", required = true)
            Contact contact) {

        if (contact == null) {
            throw new RestServiceException("Bad Request", Response.Status.BAD_REQUEST);
        }

        Response.ResponseBuilder builder;

        try {
            // Clear the ID if accidentally set
            contact.setId(null);

            // Go add the new Contact.
            service.create(contact);

            // Create a "Resource Created" 201 Response and pass the contact back in case it is needed.
            builder = Response.status(Response.Status.CREATED).entity(contact);


        } catch (ConstraintViolationException ce) {
            //Handle bean validation issues
            Map<String, String> responseObj = new HashMap<>();

            for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);

        } catch (UniqueEmailException e) {
            // Handle the unique constraint violation
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("email", "That email is already used, please use a unique email");
            throw new RestServiceException("Bad Request", responseObj, Response.Status.CONFLICT, e);
        } catch (InvalidAreaCodeException e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("area_code", "The telephone area code provided is not recognised, please provide another");
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, e);
        } catch (Exception e) {
            // Handle generic exceptions
            throw new RestServiceException(e);
        }

        log.info("createContact completed. Contact = " + contact);
        return builder.build();
    }

    /**
     * <p>Updates the contact with the ID provided in the database. Performs validation, and will return a JAX-RS response
     * with either 200 (ok), or with a map of fields, and related errors.</p>
     *
     * @param contact The Contact object, constructed automatically from JSON input, to be <i>updated</i> via
     * {@link ContactService#update(Contact)}
     * @param id The long parameter value provided as the id of the Contact to be updated
     * @return A Response indicating the outcome of the create operation
     */
    @PUT
    @Path("/{id:[0-9]+}")
    @Operation(description = "Update a Contact in the database")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Contact updated successfully"),
            @APIResponse(responseCode = "400", description = "Invalid Contact supplied in request body"),
            @APIResponse(responseCode = "404", description = "Contact with id not found"),
            @APIResponse(responseCode = "409", description = "Contact details supplied in request body conflict with another existing Contact"),
            @APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request")
    })
    @Transactional
    public Response updateContact(
            @Parameter(description=  "Id of Contact to be updated", required = true)
            @Schema(minimum = "0")
            @PathParam("id")
            long id,
            @Parameter(description = "JSON representation of Contact object to be updated in the database", required = true)
            Contact contact) {

        if (contact == null || contact.getId() == null) {
            throw new RestServiceException("Invalid Contact supplied in request body", Response.Status.BAD_REQUEST);
        }

        if (contact.getId() != null && contact.getId() != id) {
            // The client attempted to update the read-only Id. This is not permitted.
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("id", "The Contact ID in the request body must match that of the Contact being updated");
            throw new RestServiceException("Contact details supplied in request body conflict with another Contact",
                    responseObj, Response.Status.CONFLICT);
        }

        if (service.findById(contact.getId()) == null) {
            // Verify that the contact exists. Return 404, if not present.
            throw new RestServiceException("No Contact with the id " + id + " was found!", Response.Status.NOT_FOUND);
        }

        Response.ResponseBuilder builder;

        try {
            // Apply the changes the Contact.
            service.update(contact);

            // Create an OK Response and pass the contact back in case it is needed.
            builder = Response.ok(contact);


        } catch (ConstraintViolationException ce) {
            //Handle bean validation issues
            Map<String, String> responseObj = new HashMap<>();

            for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);
        } catch (UniqueEmailException e) {
            // Handle the unique constraint violation
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("email", "That email is already used, please use a unique email");
            throw new RestServiceException("Contact details supplied in request body conflict with another Contact",
                    responseObj, Response.Status.CONFLICT, e);
        } catch (InvalidAreaCodeException e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("area_code", "The telephone area code provided is not recognised, please provide another");
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, e);
        } catch (Exception e) {
            // Handle generic exceptions
            throw new RestServiceException(e);
        }

        log.info("updateContact completed. Contact = " + contact);
        return builder.build();
    }

    /**
     * <p>Deletes a contact using the ID provided. If the ID is not present then nothing can be deleted.</p>
     *
     * <p>Will return a JAX-RS response with either 204 NO CONTENT or with a map of fields, and related errors.</p>
     *
     * @param id The Long parameter value provided as the id of the Contact to be deleted
     * @return A Response indicating the outcome of the delete operation
     */
    @DELETE
    @Path("/{id:[0-9]+}")
    @Operation(description = "Delete a Contact from the database")
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "The contact has been successfully deleted"),
            @APIResponse(responseCode = "400", description = "Invalid Contact id supplied"),
            @APIResponse(responseCode = "404", description = "Contact with id not found"),
            @APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request")
    })
    @Transactional
    public Response deleteContact(
            @Parameter(description = "Id of Contact to be deleted", required = true)
            @Schema(minimum = "0")
            @PathParam("id")
            long id) {

        Response.ResponseBuilder builder;

        Contact contact = service.findById(id);
        if (contact == null) {
            // Verify that the contact exists. Return 404, if not present.
            throw new RestServiceException("No Contact with the id " + id + " was found!", Response.Status.NOT_FOUND);
        }

        try {
            service.delete(contact);

            builder = Response.noContent();

        } catch (Exception e) {
            // Handle generic exceptions
            throw new RestServiceException(e);
        }
        log.info("deleteContact completed. Contact = " + contact);
        return builder.build();
    }
}