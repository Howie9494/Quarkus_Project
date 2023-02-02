package uk.ac.newcastle.enterprisemiddleware.taxi;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;

/**
 * <p>Clientside representation of an TaxiBookingService object pulled from an external RESTFul API.</p>
 *
 * <p>This is the mirror opposite of a server side JAX-RS service</p>
 *
 * @author howie
 */
@RegisterRestClient(configKey = "taxi-api")
public interface TaxiService {

    @GET
    @Path("/customers/email/{email}")
    TaxiCustomer findCustomerByEmail(@PathParam("email") String email);

    @POST
    @Path("/guestBookings")
    TaxiBooking createTaxiGuestBooking(TaxiGuestBooking taxiGuestBooking);

    @POST
    @Path("/bookings")
    TaxiBooking createTaxiBooking(TaxiBooking taxiBooking);

    @DELETE
    @Path("/bookings/{id}")
    void cancelTaxiBooking(@PathParam("id")Long id);
}
