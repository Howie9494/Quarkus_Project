package uk.ac.newcastle.enterprisemiddleware.customer;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.jboss.resteasy.reactive.Cache;
import uk.ac.newcastle.enterprisemiddleware.booking.Booking;
import uk.ac.newcastle.enterprisemiddleware.booking.BookingService;
import uk.ac.newcastle.enterprisemiddleware.util.RestServiceException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * <p>This class produces a RESTful service exposing the functionality of {@link CustomerService}.</p>
 *
 * @author Howie
 * @see CustomerService
 * @see javax.ws.rs.core.Response
 */
@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerRestService {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    CustomerService service;

    @Inject
    BookingService bookingService;

    /**
     * <p>Return all the Customers.  They are sorted alphabetically by Id.</p>
     *
     * @return A Response containing a list of Customers
     */
    @GET
    @Operation(summary = "Fetch all Customers", description = "Returns a JSON array of all stored Customer objects.")
    public Response retrieveAllCustomers(@QueryParam("firstName") String firstName, @QueryParam("lastName") String lastName) {
        log.info("CustomerRestService -- retrieveAllCustomers starts execution.");
        List<Customer> customers;

        if(firstName == null && lastName == null) {
            customers = service.findAllCustomer();
        } else if(lastName == null) {
            customers = service.findAllByFirstName(firstName);
        } else if(firstName == null) {
            customers = service.findAllByLastName(lastName);
        } else {
            customers = service.findAllByFirstName(firstName);
            customers.retainAll(service.findAllByLastName(lastName));
        }
        log.info("CustomerRestService -- retrieveAllCustomers completes execution.");
        return Response.ok(customers).build();
    }

    /**
     * <p>Search for and return a Customer identified by email address.<p/>
     *
     * @param email The string parameter value provided as a Customer's email
     * @return A Response containing a single Customer
     */
    @GET
    @Cache
    @Path("/email/{email:.+[%40|@].+}")
    @Operation(
            summary = "Fetch a Customer by Email",
            description = "Returns a JSON representation of the Customer object with the provided email."
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description ="Customer found"),
            @APIResponse(responseCode = "404", description = "Customer with email not found")
    })
    public Response retrieveCustomersByEmail(
            @Parameter(description = "Email of Customer to be fetched", required = true)
            @PathParam("email") String email) {
        log.info("CustomerRestService -- retrieveCustomersByEmail starts execution.");
        Customer customer;
        try {
            customer = service.findByEmail(email);
        } catch (NoResultException e) {
            throw new RestServiceException("No Customer with the email " + email + " was found!", Response.Status.NOT_FOUND);
        }
        log.info("CustomerRestService -- retrieveCustomersByEmail completes execution.");
        return Response.ok(customer).build();
    }

    /**
     * <p>Search for and return a Customer identified by id.</p>
     *
     * @param id The long parameter value provided as a Customer's id
     * @return A Response containing a single Customer
     */
    @GET
    @Cache
    @Path("/id/{id:[0-9]+}")
    @Operation(
            summary = "Fetch a Customer by id",
            description = "Returns a JSON representation of the Customer object with the provided id."
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description ="Customer found"),
            @APIResponse(responseCode = "404", description = "Customer with id not found")
    })
    public Response retrieveCustomerById(
            @Parameter(description = "Id of Customer to be fetched")
            @Schema(minimum = "0", required = true)
            @PathParam("id") long id) {
        log.info("CustomerRestService -- retrieveCustomerById starts execution.");
        Customer customer = service.findById(id);
        if (customer == null) {
            throw new RestServiceException("No Customer with the id " + id + " was found!", Response.Status.NOT_FOUND);
        }
        log.info("findById " + id + ": found Customer = " + customer);
        return Response.ok(customer).build();
    }

    /**
     * <p>Creates a new Customer from the values provided. Performs validation and will return a JAX-RS response with
     * either 201 (Resource created) or with a map of fields, and related errors.</p>
     *
     * @param customer The Customer object, constructed automatically from JSON input, to be <i>created</i> via
     * {@link CustomerService#create(Customer)}
     * @return A Response indicating the outcome of the create operation
     */
    @POST
    @Operation(description = "Add a new Customer to the database")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Customer created successfully."),
            @APIResponse(responseCode = "400", description = "Invalid Customer supplied in request body"),
            @APIResponse(responseCode = "409", description = "Customer supplied in request body conflicts with an existing Customer"),
            @APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request")
    })
    @Transactional
    public Response createCustomer(
            @Parameter(description = "JSON representation of Customer object to be added to the database", required = true)
                    Customer customer) {

        log.info("CustomerRestService -- createCustomer starts execution.");

        if (customer == null) {
            throw new RestServiceException("Bad Request", Response.Status.BAD_REQUEST);
        }

        Response.ResponseBuilder builder;
        try {

            customer.setId(null);

            service.create(customer);

            builder = Response.status(Response.Status.CREATED).entity(customer);

        } catch (ConstraintViolationException ce) {
            Map<String, String> responseObj = new HashMap<>();
            for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);
        } catch (UniqueEmailException e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("unique issue", "That email is already used, please use a unique");
            throw new RestServiceException("Bad Request", responseObj, Response.Status.CONFLICT, e);
        }  catch (Exception e) {
            throw new RestServiceException(e);
        }

        log.info("CustomerRestService -- createCustomer completes execution. Customer = " + customer);
        return builder.build();
    }

    /**
     * <p>Updates the Customer with the ID provided in the database. Performs validation, and will return a JAX-RS response
     * with either 200 (ok), or with a map of fields, and related errors.</p>
     *
     * @param customer The Customer object, constructed automatically from JSON input, to be <i>updated</i> via
     * {@link CustomerService#update(Customer)}
     * @param id The long parameter value provided as the id of the Customer to be updated
     * @return A Response indicating the outcome of the create operation
     */
    @PUT
    @Path("/{id:[0-9]+}")
    @Operation(description = "Update a Customer in the database")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Customer updated successfully"),
            @APIResponse(responseCode = "400", description = "Invalid Customer supplied in request body"),
            @APIResponse(responseCode = "404", description = "Customer with id not found"),
            @APIResponse(responseCode = "409", description = "Customer details supplied in request body conflict with another existing Customer"),
            @APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request")
    })
    @Transactional
    public Response updateCustomer(
            @Parameter(description=  "Id of Customer to be updated", required = true)
            @Schema(minimum = "0")
            @PathParam("id")
                    long id,
            @Parameter(description = "JSON representation of Customer object to be updated in the database", required = true)
                    Customer customer) {

        if (customer == null || customer.getId() == null) {
            throw new RestServiceException("Invalid customer supplied in request body", Response.Status.BAD_REQUEST);
        }

        if (customer.getId() != null && customer.getId() != id) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("id", "The customer ID in the request body must match that of the customer being updated");
            throw new RestServiceException("customer details supplied in request body conflict with another customer",
                    responseObj, Response.Status.CONFLICT);
        }

        if (service.findById(customer.getId()) == null) {
            throw new RestServiceException("No customer with the id " + id + " was found!", Response.Status.NOT_FOUND);
        }

        Response.ResponseBuilder builder;

        try {
            service.update(customer);

            builder = Response.ok(customer);
        } catch (ConstraintViolationException ce) {
            Map<String, String> responseObj = new HashMap<>();

            for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);
        } catch (UniqueEmailException e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("email", "That email is already used, please use a unique email");
            throw new RestServiceException("customer details supplied in request body conflict with another customer",
                    responseObj, Response.Status.CONFLICT, e);
        }  catch (Exception e) {
            throw new RestServiceException(e);
        }

        log.info("updateCustomer completed. customer = " + customer);
        return builder.build();
    }

    /**
     * <p>Deletes a Customer using the ID provided. If the ID is not present then nothing can be deleted.</p>
     *
     * <p>Will return a JAX-RS response with either 204 NO CONTENT or with a map of fields, and related errors.</p>
     *
     * @param id The Long parameter value provided as the id of the Customer to be deleted
     * @return A Response indicating the outcome of the delete operation
     */
    @DELETE
    @Path("/{id:[0-9]+}")
    @Operation(description = "Delete a Customer from the database")
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "The Customer has been successfully deleted"),
            @APIResponse(responseCode = "400", description = "Invalid Customer id supplied"),
            @APIResponse(responseCode = "404", description = "Customer with id not found"),
            @APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request")
    })
    @Transactional
    public Response deleteCustomer(
            @Parameter(description = "Id of Hotel to be deleted", required = true)
            @Schema(minimum = "0")
            @PathParam("id")
                    long id) {

        Response.ResponseBuilder builder;

        Customer customer = service.findById(id);
        if (customer == null) {
            throw new RestServiceException("No customer with the id " + id + " was found!", Response.Status.NOT_FOUND);
        }

        try {
            service.delete(customer);

            builder = Response.noContent();

        } catch (Exception e) {
            throw new RestServiceException(e);
        }
        log.info("deleteContact completed. customer = " + customer);
        return builder.build();
    }
}
