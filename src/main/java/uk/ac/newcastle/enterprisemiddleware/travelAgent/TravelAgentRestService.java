package uk.ac.newcastle.enterprisemiddleware.travelAgent;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.Cache;
import uk.ac.newcastle.enterprisemiddleware.booking.Booking;
import uk.ac.newcastle.enterprisemiddleware.booking.BookingService;
import uk.ac.newcastle.enterprisemiddleware.booking.UniqueHotelAndDateException;
import uk.ac.newcastle.enterprisemiddleware.customer.Customer;
import uk.ac.newcastle.enterprisemiddleware.customer.CustomerService;
import uk.ac.newcastle.enterprisemiddleware.customer.UniqueEmailException;
import uk.ac.newcastle.enterprisemiddleware.flight.FlightBooking;
import uk.ac.newcastle.enterprisemiddleware.flight.FlightCustomer;
import uk.ac.newcastle.enterprisemiddleware.flight.FlightGuestBooking;
import uk.ac.newcastle.enterprisemiddleware.flight.FlightService;
import uk.ac.newcastle.enterprisemiddleware.guestBooking.GuestBooking;
import uk.ac.newcastle.enterprisemiddleware.taxi.TaxiBooking;
import uk.ac.newcastle.enterprisemiddleware.taxi.TaxiCustomer;
import uk.ac.newcastle.enterprisemiddleware.taxi.TaxiGuestBooking;
import uk.ac.newcastle.enterprisemiddleware.taxi.TaxiService;
import uk.ac.newcastle.enterprisemiddleware.util.RestServiceException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.transaction.SystemException;
import javax.transaction.Transactional;
import javax.transaction.UserTransaction;
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
 * @author Howie
 */
@Path("/travelAgent")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TravelAgentRestService {

    private static final Map<String,Long> CUSTOMER_IDS = new HashMap<>();

    @Inject
    @Named("logger")
    Logger log;

    @RestClient
    FlightService flightService;

    @RestClient
    TaxiService taxiService;

    @Inject
    CustomerService customerService;

    @Inject
    BookingService bookingService;

    @Inject
    TravelAgentBookingService travelAgentBookingService;

    /**
     * <p>Return all the Hotels.  They are sorted alphabetically by Id.</p>
     *
     * @return A Response containing a list of hotels
     */
    @GET
    @Cache
    @Operation(summary = "Fetch all TravelAgentBookings", description = "Returns a JSON array of all stored TravelAgentBooking objects.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description ="TravelAgentBooking found"),
            @APIResponse(responseCode = "500", description = "TravelAgentBooking with date not found")
    })
    public Response retrieveAllTravelAgentBookings(@QueryParam("hotelCustomerId") Long hotelCustomerId,
                                               @QueryParam("taxiCustomerId") Long taxiCustomerId,@QueryParam("flightCustomerId") Long flightCustomerId){
        log.info("TravelAgentRestService -- retrieveAllTravelAgentBookings starts execution.");
        List<TravelAgentBooking> travelAgentBookings = travelAgentBookingService.findAllTravelAgentBooking();
        if(hotelCustomerId != null){
            travelAgentBookings = travelAgentBookings.stream().filter(e -> e.getHotelCustomerId().equals(hotelCustomerId)).collect(Collectors.toList());
        }
        if(taxiCustomerId != null){
            travelAgentBookings = travelAgentBookings.stream().filter(e -> e.getTaxiCustomerId().equals(taxiCustomerId)).collect(Collectors.toList());
        }
        if(flightCustomerId != null){
            travelAgentBookings = travelAgentBookings.stream().filter(e -> e.getFlightCustomerId().equals(flightCustomerId)).collect(Collectors.toList());
        }
        log.info("TravelAgentRestService -- retrieveAllTravelAgentBookings completes execution.");
        return Response.ok(travelAgentBookings).build();
    }

    @POST
    @Operation(description = "Book hotels,flight and taxis")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Customer and Booking created successfully."),
            @APIResponse(responseCode = "400", description = "Invalid GuestBooking supplied in request body"),
            @APIResponse(responseCode = "404", description = "Hotel with id not found"),
            @APIResponse(responseCode = "409", description = "email or hotel and date supplied in request body conflicts with an existing record"),
            @APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request")
    })
    @Transactional
    public Response createTravelAgentBooking(
            @Parameter(description = "JSON representation of the information needed to book hotels,flights and taxis", required = true)
                    TravelAgent travelAgent) throws Exception {

        log.info("TravelAgentRestService - createTravelAgent begins to execution");

        Response.ResponseBuilder builder;
        TravelAgentBooking travelAgentBooking = new TravelAgentBooking();

        Booking hotelBooking;
        FlightBooking flightBooking;
        TaxiBooking taxiBooking;

        try{
            log.info("createTravelAgent - createHotelBooking begins to execution");

            Customer existCustomer = null;
            try{
                existCustomer = customerService.findByEmail(travelAgent.getEmail());
            }catch (NoResultException e) {
                // ignore
            }

            if(existCustomer == null){
                Customer inputCustomer = new Customer(travelAgent.getFirstName(),
                        travelAgent.getLastName(),
                        travelAgent.getEmail(),
                        travelAgent.getPhoneNumber());

                Customer customer = customerService.create(inputCustomer);

                CUSTOMER_IDS.put("hotelCustomerId",customer.getId());
            }else {
                CUSTOMER_IDS.put("hotelCustomerId",existCustomer.getId());
            }
            Long hotelCustomerId = CUSTOMER_IDS.get("hotelCustomerId");

            hotelBooking = bookingService.create(new Booking(hotelCustomerId,
                    travelAgent.getHotelId(), travelAgent.getHotelBookingDate()));
            travelAgentBooking.setHotelCustomerId(hotelCustomerId);
            travelAgentBooking.setHotelBookingId(hotelBooking.getId());

            log.info("createTravelAgent - createHotelBooking completes to execution");
        }catch (ConstraintViolationException ce) {
            Map<String, String> responseObj = new HashMap<>();
            for (ConstraintViolation<?> violation : ce.getConstraintViolations()) {
                responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            throw new RestServiceException("Bad Request", responseObj, Response.Status.BAD_REQUEST, ce);
        } catch (UniqueEmailException e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("unique issue", "That email is already used, please use a unique");
            throw new RestServiceException("Bad Request", responseObj, Response.Status.CONFLICT, e);
        }  catch (UniqueHotelAndDateException e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("hotelAndDate", "That hotelAndDate is already used, please use a unique hotelAndDate");
            throw new RestServiceException("hotel details supplied in request body conflict with another booking",
                    responseObj, Response.Status.CONFLICT, e);
        }catch (Exception e) {
            throw new RestServiceException(e.getMessage());
        }

        try{
            log.info("createTravelAgent - createFlightBooking begins to execution");

            FlightCustomer existCustomer = null;
            try{
                log.info("createFlightBooking - findCustomerByEmail");
                existCustomer = flightService.findCustomerByEmail(travelAgent.getEmail());
            }catch (Exception e) {
                /*if(e.getResponse().getStatusInfo() == Response.Status.NOT_FOUND){
                    // ignore
                }else {
                    throw new RestServiceException(e.getMessage());
                }*/
            }

            if(existCustomer == null){
                log.info("createFlightBooking - createFlightGuestBooking");
                FlightGuestBooking flightGuestBooking = new FlightGuestBooking(travelAgent.getFirstName(),
                        travelAgent.getLastName(),travelAgent.getEmail(),travelAgent.getPhoneNumber(),
                        travelAgent.getHotelId(),travelAgent.getFlightBookingDate());
                flightBooking = flightService.createFlightGuestBooking(flightGuestBooking);
                CUSTOMER_IDS.put("flightCustomerId",flightBooking.getContactId());
            }else {
                CUSTOMER_IDS.put("flightCustomerId",existCustomer.getId());
                log.info("createFlightBooking - createFlightBooking");
                Long flightCustomerId = CUSTOMER_IDS.get("flightCustomerId");
                flightBooking = flightService.createFlightBooking(new FlightBooking(flightCustomerId, travelAgent.getHotelId(), travelAgent.getFlightBookingDate()));
            }
            travelAgentBooking.setFlightCustomerId(flightBooking.getContactId());
            travelAgentBooking.setFlightBookingId(flightBooking.getId());
            log.info("createTravelAgent - createFlightBooking completes to execution");
        }catch (Exception e) {
            log.info("createTravelAgent - createFlightBooking begins to rollback");

            bookingService.delete(hotelBooking);

            log.info("createTravelAgent - createFlightBooking completes to rollback");

            throw new RestServiceException(e.getMessage());
        }

        try{
            log.info("createTravelAgent - createTaxiBooking begins to execution");

            TaxiCustomer existCustomer = null;
            try{
                log.info("createTaxiBooking - findCustomerByEmail");
                existCustomer = taxiService.findCustomerByEmail(travelAgent.getEmail());
            }catch (Exception e) {
                /*if(e.getResponse().getStatusInfo() == Response.Status.NOT_FOUND){
                    // ignore
                }else {
                    throw new RestServiceException(e.getMessage());
                }*/
            }

            if(existCustomer == null){
                log.info("createTaxiBooking - createTaxiGuestBooking");
                TaxiGuestBooking taxiGuestBooking = new TaxiGuestBooking(travelAgent.getFirstName(),travelAgent.getLastName(),
                        travelAgent.getEmail(),travelAgent.getPhoneNumber(),travelAgent.getBirthDate(),travelAgent.getTaxiId(),travelAgent.getTaxiBookingDate());
                taxiBooking = taxiService.createTaxiGuestBooking(taxiGuestBooking);
                CUSTOMER_IDS.put("taxiCustomerId",taxiBooking.getCustomerId());
            }else {
                log.info("createTaxiBooking - createTaxiBooking");
                CUSTOMER_IDS.put("taxiCustomerId",existCustomer.getId());
                Long taxiCustomerId = CUSTOMER_IDS.get("taxiCustomerId");
                taxiBooking = taxiService.createTaxiBooking(new TaxiBooking(travelAgent.getTaxiId(), taxiCustomerId, travelAgent.getTaxiBookingDate()));
            }
            travelAgentBooking.setTaxiCustomerId(taxiBooking.getCustomerId());
            travelAgentBooking.setTaxiBookingId(taxiBooking.getId());
            log.info("createTravelAgent - createTaxiBooking completes to execution");
        }catch (Exception e) {
            log.info("createTravelAgent - createTaxiBooking begins to rollback");

            bookingService.delete(hotelBooking);
            flightService.cancelFlightBooking(flightBooking.getId());

            log.info("createTravelAgent - createTaxiBooking completes to rollback");

            throw new RestServiceException(e.getMessage());
        }


        try {
            log.info("createTravelAgent - createTravelAgentBooking begins to execution");

            travelAgentBookingService.create(travelAgentBooking);
            builder = Response.status(Response.Status.CREATED).entity(travelAgentBooking);

            log.info("createTravelAgent - createTravelAgentBooking completes to execution");
        } catch (Exception e) {
            log.info("createTravelAgent - createTravelAgentBooking begins to rollback");

            bookingService.delete(hotelBooking);
            flightService.cancelFlightBooking(flightBooking.getId());
            taxiService.cancelTaxiBooking(taxiBooking.getId());

            log.info("createTravelAgent - createTravelAgentBooking completes to rollback");

            throw new RestServiceException(e.getMessage());
        }

        log.info("TravelAgentRestService - createTravelAgent completes to execution");
        return builder.build();

    }

    /**
     * <p>Deletes a travelAgentBooking using the ID provided. If the ID is not present then nothing can be deleted.</p>
     *
     * <p>Will return a JAX-RS response with either 204 NO CONTENT or with a map of fields, and related errors.</p>
     *
     * @param id The Long parameter value provided as the id of the travelAgentBooking to be deleted
     * @return A Response indicating the outcome of the delete operation
     */
    @DELETE
    @Cache
    @Path("/{id:[0-9]+}")
    @Operation(description = "Delete a travelAgentBooking from the database")
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "The travelAgentBooking has been successfully deleted"),
            @APIResponse(responseCode = "400", description = "Invalid travelAgentBooking id supplied"),
            @APIResponse(responseCode = "404", description = "travelAgentBooking with id not found"),
            @APIResponse(responseCode = "500", description = "An unexpected error occurred whilst processing the request")
    })
    @Transactional
    public Response deleteTravelAgentBooking(
            @Parameter(description = "Id of travelAgentBooking to be deleted", required = true)
            @Schema(minimum = "0")
            @PathParam("id")
                    long id) {

        log.info("TravelAgentRestService - deleteTravelAgentBooking completes to execution");
        Response.ResponseBuilder builder;

        TravelAgentBooking travelAgentBooking = travelAgentBookingService.findById(id);
        if (travelAgentBooking == null) {
            throw new RestServiceException("No travelAgentBooking with the id " + id + " was found!", Response.Status.NOT_FOUND);
        }

        try{
            log.info("deleteTravelAgentBooking - cancelTaxiBooking begins to execution");
            taxiService.cancelTaxiBooking(travelAgentBooking.getTaxiBookingId());
            log.info("deleteTravelAgentBooking - cancelFlightBooking begins to execution");
            flightService.cancelFlightBooking(travelAgentBooking.getFlightBookingId());
            log.info("deleteTravelAgentBooking - delete bookingService begins to execution");
            bookingService.delete(bookingService.findById(travelAgentBooking.getHotelBookingId()));
            log.info("deleteTravelAgentBooking - delete travelAgentBookingService begins to execution");
            travelAgentBookingService.delete(travelAgentBooking);
            builder = Response.noContent();
        }catch (Exception e) {
            throw new RestServiceException(e);
        }

        log.info("deleteContact completed. travelAgentBooking = " + travelAgentBooking);
        return builder.build();
    }

}
