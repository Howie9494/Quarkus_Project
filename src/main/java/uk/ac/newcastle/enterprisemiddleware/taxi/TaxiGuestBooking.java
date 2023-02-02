package uk.ac.newcastle.enterprisemiddleware.taxi;

import java.io.Serializable;
import java.util.Date;

/**
 * {
 *   "firstName": "string",
 *   "lastName": "string",
 *   "email": "string",
 *   "phoneNumber": "string",
 *   "birthDate": "2022-11-14",
 *   "taxiId": 0,
 *   "bookingDate": "2022-11-14"
 * }
 */
public class TaxiGuestBooking implements Serializable {

    private static final long serialVersionUID = 13445654365335L;

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private Date birthDate;

    private Long taxiId;

    private Date bookingDate;

    public TaxiGuestBooking() {
    }

    public TaxiGuestBooking(String firstName, String lastName, String email, String phoneNumber, Date birthDate, Long taxiId, Date bookingDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.taxiId = taxiId;
        this.bookingDate = bookingDate;
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

    public void setBirthDate(Date birthDay) {
        this.birthDate = birthDay;
    }

    public Long getTaxiId() {
        return taxiId;
    }

    public void setTaxiId(Long taxiId) {
        this.taxiId = taxiId;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }
}
