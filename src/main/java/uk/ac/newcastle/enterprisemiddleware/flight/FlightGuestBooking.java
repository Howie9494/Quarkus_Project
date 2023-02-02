package uk.ac.newcastle.enterprisemiddleware.flight;

import java.io.Serializable;
import java.util.Date;

/**
 *{
 *   "firstName": "string",
 *   "lastName": "string",
 *   "email": "string",
 *   "phoneNumber": "string",
 *   "hotelId": 0,
 *   "bookingDate": "2022-11-14"
 * }
 */
public class FlightGuestBooking implements Serializable {
    private static final long serialVersionUID = 134743543346325L;

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private Long hotelId;

    private Date bookingDate;

    public FlightGuestBooking() {
    }

    public FlightGuestBooking(String firstName, String lastName, String email, String phoneNumber, Long hotelId, Date bookingDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.hotelId = hotelId;
        this.bookingDate = bookingDate;
    }

    /**
     * @see FlightGuestBooking#getFirstName()
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @see FlightGuestBooking#setFirstName(String)
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @see FlightGuestBooking#getLastName()
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @see FlightGuestBooking#setLastName(String)
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @see FlightGuestBooking#getEmail()
     */
    public String getEmail() {
        return email;
    }

    /**
     * @see FlightGuestBooking#setEmail(String)
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @see FlightGuestBooking#getPhoneNumber()
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @see FlightGuestBooking#setPhoneNumber(String)
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @see FlightGuestBooking#getHotelId()
     */
    public Long getHotelId() {
        return hotelId;
    }

    /**
     * @see FlightGuestBooking#setHotelId(Long)
     */
    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    /**
     * @see FlightGuestBooking#getBookingDate()
     */
    public Date getBookingDate() {
        return bookingDate;
    }

    /**
     * @see FlightGuestBooking#setBookingDate(Date)
     */
    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }
}