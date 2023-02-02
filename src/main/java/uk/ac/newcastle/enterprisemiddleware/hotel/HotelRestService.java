package uk.ac.newcastle.enterprisemiddleware.hotel;

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
import java.util.stream.Collectors;

/**
 * <p>This class produces a RESTful service exposing the functionality of {@link HotelService}.</p>
 *
 * @author Howie
 * @see HotelService
 * @see javax.ws.rs.core.Response
 */
@Path("/hotels")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HotelRestService {
    @Inject
    @Named("logger")
    Logger log;

    @Inject
    HotelService service;

    @Inject
    BookingService bookingService;

    /**
     * <p>Return all the Hotels.  They are sorted alphabetically by Id.</p>
     *
     * @return A Response containing a list of hotels
     */
    @GET
    @Cache
    @Operation(summary = "Fetch all Hotels", description = "Returns a JSON array of all stored Hotel objects.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description ="Hotel found"),
            @APIResponse(responseCode = "500", description = "Hotel with date not found")
    })
    public Response retrieveAllHotels(@QueryParam("hotelName") String hotelName, @QueryParam("postcode") String postcode){
        log.info("HotelRestService -- retrieveAllHotels starts execution.");
        List<Hotel> hotels = service.findAllHotel();
        if(hotelName != null){
            hotels = hotels.stream().filter(e -> e.getHotelName().equalsIgnoreCase(hotelName)).collect(Collectors.toList());
        }
        if(postcode != null){
            hotels = hotels.stream().filter(e -> e.getPostcode().equals(postcode)).collect(Collectors.toList());
        }
        log.info("HotelRestService -- retrieveAllHotels completes execution.");
        return Response.ok(hotels).build();
    }

    /**
     * <p>Search for and return a Hotel identified by id.</p>
     *
     * @param id The String parameter value provided as a Hotel's id
     * @return A Response containing a single Hotel
     */
    @GET
    @Cache
    @Path("/id/{id}")
    @Operation(
            summary = "Fetch a Hotel by id",
            description = "Returns a JSON representation of the Hotel object with the provided id."
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description ="Hotel found"),
            @APIResponse(responseCode = "404", description = "Hotel with id not found")
    })
    public Response retrieveHotelById(
            @Parameter(description = "Id of Hotel to be fetched")
            @PathParam("id") Long id) {
        log.info("HotelRestService -- retrieveHotelById starts execution.");
        Hotel hotel = service.findById(id);
        if (hotel == null) {
            throw new RestServiceException("No Hotel with the id " + id + " was found!", Response.Status.NOT_FOUND);
        }
        log.info("findById " + id + ": found Hotel = " + hotel);
        return Response.ok(hotel).build();
    }

    /**
     * <p>Search for and return a Hotel identified by phoneNumber.</p>
     *
     * @param phoneNumber The String parameter value provided as a Hotel's phoneNumber
     * @return A Response containing a single Hotel
     */
    @GET
    @Cache
    @Path("/phoneNumber/{phoneNumber}")
    @Operation(
            summary = "Fetch a Hotel by phoneNumber",
            description = "Returns a JSON representation of the Hotel object with the provided phoneNumber."
    )
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description ="Hotel found"),
            @APIResponse(responseCode = "404", description = "Hotel with phoneNumber not found")
    })
    public Response retrieveHotelByPhoneNumber(
            @Parameter(description = "phoneNumber of Hotel to be fetched")
            @PathParam("phoneNumber")
                    String phoneNumber) {
        log.info("HotelRestService -- retrieveHotelByPhoneNumber starts execution.");
        Hotel hotel = service.findByPhoneNumber(phoneNumber);
        if (hotel == null) {
            throw new RestServiceException("No Hotel with the phoneNumber " + phoneNumber + " was found!", Response.Status.NOT_FOUND);
        }
        log.info("findByPhoneNumber " + phoneNumber + ": found Hotel = " + hotel);
        return Response.ok(hotel).build();
    }

    /**
     * <p>Creates a new Hotel from the values provided. Performs validation and will return a JAX-RS response with
     * either 201 (Resource created) or with a map of fields, and related errors.</p>
     *
     * @param hotel The Hotel object, constructed automatically from JSON input, to be <i>created</i> via
     * {@link HotelService#create(Hotel)}
     * @return A Response indicating the outcome of the create operation
     */
    @POST
    @Operation(description = "Add a new Hotel to the database")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Hotel created successfully."),
            @APIResponse(responseCode = "400", description = "Invalid Hotel supplied in request body"),
            @APIResponse(responseCode = "409", description = "Hotel supplied in request body conflicts with an existing Hotel"),
            @APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request")
    })
    @Transactional
    public Response createHotel(
            @Parameter(description = "JSON representation of Hotel object to be added to the database", required = true)
                    Hotel hotel) {

        log.info("HotelRestService -- createHotel starts execution.");

        if (hotel == null) {
            throw new RestServiceException("Bad Request", Response.Status.BAD_REQUEST);
        }

        Response.ResponseBuilder builder;
        try {

            hotel.setId(null);

            service.create(hotel);

            builder = Response.status(Response.Status.CREATED).entity(hotel);

        } catch (ConstraintViolationException ce) {
            Map<String, String> responseObj = new HashMap<>();
            for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);
        }catch (UniquePhoneNumberException e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("phoneNumber", "That phoneNumber is already used, please use a unique phoneNumber");
            throw new RestServiceException("hotel details supplied in request body conflict with another hotel",
                    responseObj, Response.Status.CONFLICT, e);
        }   catch (Exception e) {
            throw new RestServiceException(e);
        }

        log.info("HotelRestService -- createHotel completes execution. Hotel = " + hotel);
        return builder.build();
    }

    /**
     * <p>Updates the Hotel with the ID provided in the database. Performs validation, and will return a JAX-RS response
     * with either 200 (ok), or with a map of fields, and related errors.</p>
     *
     * @param hotel The Hotel object, constructed automatically from JSON input, to be <i>updated</i> via
     * {@link HotelService#update(Hotel)}
     * @param id The long parameter value provided as the id of the Hotel to be updated
     * @return A Response indicating the outcome of the create operation
     */
    @PUT
    @Cache
    @Path("/{id:[0-9]+}")
    @Operation(description = "Update a Hotel in the database")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Hotel updated successfully"),
            @APIResponse(responseCode = "400", description = "Invalid Hotel supplied in request body"),
            @APIResponse(responseCode = "404", description = "Hotel with id not found"),
            @APIResponse(responseCode = "409", description = "Hotel details supplied in request body conflict with another existing Hotel"),
            @APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request")
    })
    @Transactional
    public Response updateHotel(
            @Parameter(description=  "Id of Hotel to be updated", required = true)
            @Schema(minimum = "0")
            @PathParam("id")
                    long id,
            @Parameter(description = "JSON representation of Hotel object to be updated in the database", required = true)
                    Hotel hotel) {

        if (hotel == null || hotel.getId() == null) {
            throw new RestServiceException("Invalid hotel supplied in request body", Response.Status.BAD_REQUEST);
        }

        if (hotel.getId() != null && hotel.getId() != id) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("id", "The hotel ID in the request body must match that of the Hotel being updated");
            throw new RestServiceException("Hotel details supplied in request body conflict with another Hotel",
                    responseObj, Response.Status.CONFLICT);
        }

        if (service.findById(hotel.getId()) == null) {
            throw new RestServiceException("No Hotel with the id " + id + " was found!", Response.Status.NOT_FOUND);
        }

        Response.ResponseBuilder builder;

        try {
            service.update(hotel);

            builder = Response.ok(hotel);
        } catch (ConstraintViolationException ce) {
            Map<String, String> responseObj = new HashMap<>();

            for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);
        } catch (UniquePhoneNumberException e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("phoneNumber", "That phoneNumber is already used, please use a unique email");
            throw new RestServiceException("hotel details supplied in request body conflict with another hotel",
                    responseObj, Response.Status.CONFLICT, e);
        }  catch (Exception e) {
            throw new RestServiceException(e);
        }

        log.info("updateHotel completed. hotel = " + hotel);
        return builder.build();
    }

    /**
     * <p>Deletes a Hotel using the ID provided. If the ID is not present then nothing can be deleted.</p>
     *
     * <p>Will return a JAX-RS response with either 204 NO CONTENT or with a map of fields, and related errors.</p>
     *
     * @param id The Long parameter value provided as the id of the Hotel to be deleted
     * @return A Response indicating the outcome of the delete operation
     */
    @DELETE
    @Cache
    @Path("/{id:[0-9]+}")
    @Operation(description = "Delete a Hotel from the database")
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "The Hotel has been successfully deleted"),
            @APIResponse(responseCode = "400", description = "Invalid Hotel id supplied"),
            @APIResponse(responseCode = "404", description = "Hotel with id not found"),
            @APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request")
    })
    @Transactional
    public Response deleteHotel(
            @Parameter(description = "Id of Hotel to be deleted", required = true)
            @Schema(minimum = "0")
            @PathParam("id")
                    long id) {

        Response.ResponseBuilder builder;

        Hotel hotel = service.findById(id);
        if (hotel == null) {
            throw new RestServiceException("No Hotel with the id " + id + " was found!", Response.Status.NOT_FOUND);
        }

        try {
            service.delete(hotel);

            builder = Response.noContent();

        } catch (Exception e) {
            throw new RestServiceException(e);
        }
        log.info("deleteContact completed. hotel = " + hotel);
        return builder.build();
    }
}
