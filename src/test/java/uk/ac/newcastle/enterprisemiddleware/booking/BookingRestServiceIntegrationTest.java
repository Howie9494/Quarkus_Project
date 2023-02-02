package uk.ac.newcastle.enterprisemiddleware.booking;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import uk.ac.newcastle.enterprisemiddleware.contact.Contact;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@TestHTTPEndpoint(BookingRestService.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@QuarkusTestResource(H2DatabaseTestResource.class)
public class BookingRestServiceIntegrationTest {

    private static Booking booking;

    @BeforeAll
    static void setup() {
        booking = new Booking();
        booking.setHotelId(1L);
        booking.setCustomerId(1L);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse("2022-12-09");
            booking.setBookingDate(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Test
    @Order(7)
    public void testCanCreateBooking() {
        given().
                contentType(ContentType.JSON).
                body(booking).
        when()
                .post().
        then().
                statusCode(201);
    }

    @Test
    @Order(8)
    public void testCanGetBookings() {
        Response response = when().
                get().
                then().
                statusCode(200).
                extract().response();

        Booking[] result = response.body().as(Booking[].class);

        System.out.println(result[0]);

        assertEquals(1, result.length);
        assertTrue(booking.getHotelId().equals(result[0].getHotelId()), "Hotel id not equal");
        assertTrue(booking.getCustomerId().equals(result[0].getCustomerId()), "Customer id not equal");
        assertTrue(booking.getBookingDate().equals(result[0].getBookingDate()), "Booking date not equal");
    }

    @Test
    @Order(9)
    public void testDuplicateHotelAndDateCausesError() {
        given().
                contentType(ContentType.JSON).
                body(booking).
        when().
                post().
        then().
                statusCode(409).
                body("reasons.hotelAndDate", containsString("Hotel and Date is already used"));
    }

    @Test
    @Order(10)
    public void testCanDeleteBooking() {
        Response response = when().
                get().
                then().
                statusCode(200).
                extract().response();

        Booking[] result = response.body().as(Booking[].class);

        when().
                delete(result[0].getId().toString()).
                then().
                statusCode(204);
    }
}
