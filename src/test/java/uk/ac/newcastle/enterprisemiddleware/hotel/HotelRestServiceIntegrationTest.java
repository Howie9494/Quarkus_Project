package uk.ac.newcastle.enterprisemiddleware.hotel;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@TestHTTPEndpoint(HotelRestService.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@QuarkusTestResource(H2DatabaseTestResource.class)
public class HotelRestServiceIntegrationTest {

    private static Hotel hotel;

    @BeforeAll
    static void setup() {
        hotel = new Hotel();
        hotel.setHotelName("Hillton");
        hotel.setPhoneNumber("(0783)343-5423");
        hotel.setPostcode("NE45SA");
    }

    @Test
    @Order(1)
    public void testCanCreateHotel() {
        given().
                contentType(ContentType.JSON).
                body(hotel).
        when()
                .post().
        then().
                statusCode(201);
    }

    @Test
    @Order(2)
    public void testCanGetHotels() {
        Response response = when().
                get().
                then().
                statusCode(200).
                extract().response();

        Hotel[] result = response.body().as(Hotel[].class);

        System.out.println(result[0]);

        assertEquals(1, result.length);
        assertTrue(hotel.getHotelName().equals(result[0].getHotelName()), "Hotel name not equal");
        assertTrue(hotel.getPhoneNumber().equals(result[0].getPhoneNumber()), "Phone number not equal");
        assertTrue(hotel.getPostcode().equals(result[0].getPostcode()), "Postcode not equal");
    }

    @Test
    @Order(3)
    public void testDuplicatePhoneNumberCausesError() {
        given().
                contentType(ContentType.JSON).
                body(hotel).
        when().
                post().
        then().
                statusCode(409).
                body("reasons.phoneNumber", containsString("phoneNumber is already used"));
    }

    @Test
    @Order(4)
    public void testCanDeleteHotel() {
        Response response = when().
                get().
                then().
                statusCode(200).
                extract().response();

        Hotel[] result = response.body().as(Hotel[].class);

        when().
                delete(result[0].getId().toString()).
                then().
                statusCode(204);
    }
}
