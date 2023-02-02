package uk.ac.newcastle.enterprisemiddleware.booking;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.jboss.resteasy.reactive.Cache;
import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.customer.CustomerService;
import uk.ac.newcastle.enterprisemiddleware.hotel.Hotel;
import uk.ac.newcastle.enterprisemiddleware.hotel.HotelService;
import uk.ac.newcastle.enterprisemiddleware.util.RestServiceException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * <p>This class produces a RESTful service exposing the functionality of {@link BookingService}.</p>
 *
 * @author Howie
 * @see BookingService
 * @see javax.ws.rs.core.Response
 */
@Path("/bookings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookingRestService {

    @Inject
    @Named("logger")
    Logger log;

    @Inject
    BookingService bookingService;

    @Inject
    HotelService hotelService;

    @Inject
    CustomerService customerService;

    /**
     * <p>Return all the Bookings.  They are sorted alphabetically by Id.</p>
     *
     * @return A Response containing a list of Bookings
     */
    @GET
    @Cache
    @Operation(summary = "Fetch all Bookings", description = "Returns a JSON array of all stored Booking objects.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description ="Booking found"),
            @APIResponse(responseCode = "400", description = "Wrong format of booking date"),
            @APIResponse(responseCode = "500", description = "Booking with date not found")
    })
    public Response retrieveAllBookings(@QueryParam("customerId")Long customerId,
                                        @QueryParam("hotelId")Long hotelId,
                                        @Parameter(description = "date format is yyyy-MM-dd")
                                        @QueryParam("bookingDate")String bookingDate) {
        log.info("BookingRestService -- retrieveAllBookings starts execution.");
        List<Booking> bookings = bookingService.findAllBooking();

        if(customerId != null){
            bookings = bookings.stream().filter(e -> e.getCustomerId().equals(customerId)).collect(Collectors.toList());
        }
        if(hotelId != null){
            bookings = bookings.stream().filter(e -> e.getHotelId().equals(hotelId)).collect(Collectors.toList());
        }
        if(bookingDate != null){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date = sdf.parse(bookingDate);
                bookings = bookings.stream().filter(e -> e.getBookingDate().equals(date)).collect(Collectors.toList());
            } catch (ParseException e) {
                throw new RestServiceException("Bad Request", Response.Status.BAD_REQUEST);
            }
        }

        log.info("BookingRestService -- retrieveAllBookings completes execution.");
        return Response.ok(bookings).build();
    }

    /**
     * <p>Search for and return a Booking identified by id.</p>
     *
     * @param id The String parameter value provided as a Booking's id
     * @return A Response containing a single Booking
     */
    @GET
    @Cache
    @Path("/id/{id}")
    @Operation(
            summary = "Fetch a Booking by id",
            description = "Returns a JSON representation of the Booking object with the provided id."
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description ="Booking found"),
            @APIResponse(responseCode = "404", description = "Booking with id not found")
    })
    public Response retrieveBookingById(
            @Parameter(description = "Id of Booking to be fetched")
            @PathParam("id") Long id) {
        log.info("BookingRestService -- retrieveBookingById starts execution.");
        Booking booking = bookingService.findById(id);
        if (booking == null) {
            throw new RestServiceException("No Booking with the id " + id + " was found!", Response.Status.NOT_FOUND);
        }
        log.info("findById " + id + ": found Booking = " + booking);
        return Response.ok(booking).build();
    }

    /**
     * <p>Creates a new Booking from the values provided. Performs validation and will return a JAX-RS response with
     * either 201 (Resource created) or with a map of fields, and related errors. update the status of hotel</p>
     *
     * @param booking The Booking object to be written to the database using a {@link BookingRepository} object
     * {@link BookingService#create(Booking)}
     * @return A Response indicating the outcome of the create operation
     */
    @POST
    @Path("/createBooking")
    @Operation(description = "Add a new Booking to the database")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Hotel created successfully."),
            @APIResponse(responseCode = "400", description = "Invalid email or hotelId supplied in request body"),
            @APIResponse(responseCode = "406",description = "The required room has been SOLD OUT"),
            @APIResponse(responseCode = "409", description = "Hotel supplied in request body conflicts with an existing Hotel"),
            @APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request")
    })
    @Transactional
    public Response createBooking(
            @Parameter(description = "JSON representation of Booking object to be added to the database", required = true)
                    Booking booking){

        log.info("BookingRestService -- createBooking starts execution.");

        Customer customer = customerService.findById(booking.getCustomerId());
        if(customer == null){
            throw new RestServiceException("No customer with the id " + booking.getCustomerId() + " was found!", Response.Status.NOT_FOUND);
        }

        Hotel hotel = hotelService.findById(booking.getHotelId());
        if(hotel == null){
            throw new RestServiceException("No hotel with the id " + booking.getHotelId() + " was found!", Response.Status.NOT_FOUND);
        }

        Response.ResponseBuilder builder;

        try {
            booking.setId(null);

            bookingService.create(booking);

            builder = Response.status(Response.Status.CREATED).entity(booking);
        } catch (ConstraintViolationException ce) {
            Map<String, String> responseObj = new HashMap<>();
            for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);
        }catch (UniqueHotelAndDateException e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("hotelAndDate", "That hotelAndDate is already used, please use a unique hotelAndDate");
            throw new RestServiceException("hotel details supplied in request body conflict with another booking",
                    responseObj, Response.Status.CONFLICT, e);
        }catch (Exception e) {
            throw new RestServiceException(e);
        }

        log.info("BookingRestService -- createBooking completes execution. booking:");
        return builder.build();
    }

    /**
     * <p>Updates the Booking with the ID provided in the database. Performs validation, and will return a JAX-RS response
     * with either 200 (ok), or with a map of fields, and related errors.</p>
     *
     * {@link BookingService#delete(Booking)}
     * @param id The long parameter value provided as the id of the Booking to be updated
     * @return A Response indicating the outcome of the create operation
     */
    @DELETE
    @Cache
    @Path("/cancelBooking/{id}")
    @Operation(description = "update the status of booking in the database")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Booking updated successfully"),
            @APIResponse(responseCode = "400", description = "Invalid id supplied in request body"),
            @APIResponse(responseCode = "404", description = "Booking with id not found"),
            @APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request")
    })
    @Transactional
    public Response cancelBooking(
            @Parameter(description= "Id of Booking to be canceled", required = true)
            @PathParam("id")
            Long id) {

        Booking booking = bookingService.findById(id);
        if(booking == null){
            throw new RestServiceException("No Booking with the id " + id + " was found!", Response.Status.NOT_FOUND);
        }

        Response.ResponseBuilder builder;

        try {
            booking = bookingService.delete(booking);

            builder = Response.ok(booking);
        } catch (ConstraintViolationException ce) {
            Map<String, String> responseObj = new HashMap<>();

            for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);
        }catch (UniqueHotelAndDateException e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("hotelAndDate", "That hotelAndDate is already used, please use a unique hotelAndDate");
            throw new RestServiceException("hotel details supplied in request body conflict with another booking",
                    responseObj, Response.Status.CONFLICT, e);
        }  catch (Exception e) {
            throw new RestServiceException(e);
        }

        log.info("cancelBooking completed. Booking = " + booking);
        return builder.build();
    }
}
