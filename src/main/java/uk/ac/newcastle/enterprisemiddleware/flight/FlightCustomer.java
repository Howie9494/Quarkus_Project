package uk.ac.newcastle.enterprisemiddleware.flight;

import java.io.Serializable;
import java.util.Date;

/**
 * {
 *   "id": 0,
 *   "firstName": "string",
 *   "lastName": "string",
 *   "email": "string",
 *   "phoneNumber": "string",
 *   "birthDate": "2022-11-14",
 *   "state": "string"
 * }
 */
public class FlightCustomer implements Serializable {

    private static final long serialVersionUID = 13442543535435L;

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private Date birthDate;

    private String state;

    public FlightCustomer() {
    }

    public FlightCustomer(String firstName, String lastName, String email, String phoneNumber, Date birthDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
