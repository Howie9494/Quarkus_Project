package uk.ac.newcastle.enterprisemiddleware.contact;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.Calendar;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@TestHTTPEndpoint(ContactRestService.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@QuarkusTestResource(H2DatabaseTestResource.class)
class ContactRestServiceIntegrationTest {

    private static Contact contact;

    @BeforeAll
    static void setup() {
        contact = new Contact();
        contact.setFirstName("Test");
        contact.setLastName("Account");
        contact.setEmail("test@email.com");
        contact.setBirthDate(Calendar.getInstance().getTime());
        contact.setPhoneNumber("(201) 123-4567");
        contact.setState("New Jersey");
    }

    @Test
    @Order(1)
    public void testCanCreateContact() {
        given().
                contentType(ContentType.JSON).
                body(contact).
        when()
                .post().
        then().
                statusCode(201);
    }

    @Test
    @Order(2)
    public void testCanGetContacts() {
        Response response = when().
                get().
        then().
                statusCode(200).
                extract().response();

        Contact[] result = response.body().as(Contact[].class);

        System.out.println(result[0]);

        assertEquals(1, result.length);
        assertTrue(contact.getFirstName().equals(result[0].getFirstName()), "First name not equal");
        assertTrue(contact.getLastName().equals(result[0].getLastName()), "Last name not equal");
        assertTrue(contact.getEmail().equals(result[0].getEmail()), "Email not equal");
        assertTrue(contact.getState().equals(result[0].getState()), "State not equal Expected " + contact.getState() + " Got " + result[0].getState());
        assertTrue(contact.getPhoneNumber().equals(result[0].getPhoneNumber()), "Phone number not equal");
    }

    @Test
    @Order(3)
    public void testDuplicateEmailCausesError() {
        given().
                contentType(ContentType.JSON).
                body(contact).
        when().
                post().
        then().
                statusCode(409).
                body("reasons.email", containsString("email is already used"));
    }

    @Test
    @Order(4)
    public void testCanDeleteContact() {
        Response response = when().
                get().
                then().
                statusCode(200).
                extract().response();

        Contact[] result = response.body().as(Contact[].class);

        when().
                delete(result[0].getId().toString()).
        then().
                statusCode(204);
    }
}