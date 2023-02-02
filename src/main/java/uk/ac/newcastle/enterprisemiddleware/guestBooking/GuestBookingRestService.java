package uk.ac.newcastle.enterprisemiddleware.guestBooking;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import uk.ac.newcastle.enterprisemiddleware.booking.Booking;
import uk.ac.newcastle.enterprisemiddleware.booking.BookingService;
import uk.ac.newcastle.enterprisemiddleware.booking.UniqueHotelAndDateException;
import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.customer.CustomerService;
import uk.ac.newcastle.enterprisemiddleware.customer.UniqueEmailException;
import uk.ac.newcastle.enterprisemiddleware.hotel.HotelService;
import uk.ac.newcastle.enterprisemiddleware.util.RestServiceException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * <p>This class produces a RESTful service exposing the functionality of {@link CustomerService} {@link BookingService} {@link HotelService}.</p>
 *
 * @author Howie
 * @see CustomerService,BookingService,HotelService
 * @see javax.ws.rs.core.Response
 */
@Path("/guestBooking")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GuestBookingRestService {
    @Inject
    @Named("logger")
    Logger log;

    @Inject
    BookingService bookingService;

    @Inject
    CustomerService customerService;

    @Inject
    HotelService hotelService;

    @Inject
    UserTransaction transaction;

    /**
     * <p>Creates a new Customer and Booking from the values provided. Performs validation and will return a JAX-RS response with
     * either 201 (Resource created) or with a map of fields, and related errors.</p>
     *
     * @param guestBooking The GuestBooking object, constructed automatically from JSON input, to be <i>created</i> via
     * {@link CustomerService#create(Customer)}
     * {@link BookingService#create(Booking)}
     * @return A Response indicating the outcome of the create operation
     */
    @POST
    @Operation(description = "Creates a customer and a booking")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Customer and Booking created successfully."),
            @APIResponse(responseCode = "400", description = "Invalid GuestBooking supplied in request body"),
            @APIResponse(responseCode = "404", description = "Hotel with id not found"),
            @APIResponse(responseCode = "409", description = "email or hotel and date supplied in request body conflicts with an existing record"),
            @APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request")
    })
    public Response createGuestBooking(
            @Parameter(description = "JSON representation of GuestBooking object to be added to the database", required = true)
                    GuestBooking guestBooking) throws SystemException {

        log.info("TravelAgentRestService1 -- createGuestBooking starts execution.");

        if (guestBooking == null) {
            throw new RestServiceException("Bad Request", Response.Status.BAD_REQUEST);
        }

        Response.ResponseBuilder builder;
        try {
            transaction.begin();

            Customer inputCustomer = new Customer(guestBooking.getFirstName(),
                    guestBooking.getLastName(),
                    guestBooking.getEmail(),
                    guestBooking.getPhoneNumber());

            Customer customer = customerService.create(inputCustomer);

            Booking inputBooking = new Booking(guestBooking.getHotelId(),customer.getId(),guestBooking.getBookingDate());

            if(hotelService.findById(inputBooking.getHotelId()) == null){
                throw new RestServiceException("No hotel with the id " + inputBooking.getHotelId() + " was found!", Response.Status.NOT_FOUND);
            }

            Booking booking = bookingService.create(inputBooking);

            builder = Response.status(Response.Status.CREATED).entity(booking);

            transaction.commit();

        } catch (ConstraintViolationException ce) {
            transaction.rollback();

            Map<String, String> responseObj = new HashMap<>();
            for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);
        }catch (UniqueEmailException e) {
            transaction.rollback();

            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("email", "That email is already used, please use a unique email");
            throw new RestServiceException("customer details supplied in request body conflict with another customer",
                    responseObj, Response.Status.CONFLICT, e);
        } catch (UniqueHotelAndDateException e) {
            transaction.rollback();

            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("hotelAndDate", "That hotelAndDate is already used, please use a unique hotelAndDate");
            throw new RestServiceException("hotel details supplied in request body conflict with another booking",
                    responseObj, Response.Status.CONFLICT, e);
        } catch (RestServiceException e){
            transaction.rollback();

            throw new RestServiceException("No hotel with the id was found!", Response.Status.NOT_FOUND);
        } catch (Exception e) {
            transaction.rollback();

            throw new RestServiceException(e);
        }

        log.info("TravelAgentRestService1 -- createGuestBooking completes execution.");
        return builder.build();
    }
}
