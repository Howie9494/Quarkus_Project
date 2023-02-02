package uk.ac.newcastle.enterprisemiddleware.flight;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;

/**
 * <p>Clientside representation of an FlightBookingService object pulled from an external RESTFul API.</p>
 *
 * <p>This is the mirror opposite of a server side JAX-RS service</p>
 *
 * @author howie
 */
@RegisterRestClient(configKey = "flight-api")
public interface FlightService {

    @GET
    @Path("/contacts/email/{email}")
    FlightCustomer findCustomerByEmail(@PathParam("email") String email);

    @POST
    @Path("/guest_bookings")
    FlightBooking createFlightGuestBooking(FlightGuestBooking flightGuestBooking);

    @POST
    @Path("/bookings")
    FlightBooking createFlightBooking(FlightBooking flightBooking);

    @DELETE
    @Path("/bookings/{id}")
    void cancelFlightBooking(@PathParam("id")Long id);
}
