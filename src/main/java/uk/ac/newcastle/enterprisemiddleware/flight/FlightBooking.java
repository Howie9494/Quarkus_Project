package uk.ac.newcastle.enterprisemiddleware.flight;

import java.io.Serializable;
import java.util.Date;

/**
 * {
 *   "id": 0,
 *   "contactId": 0,
 *   "hotelId": 0,
 *   "bookingDate": "2022-11-14",
 *   "bookedHotel": "string"
 * }
 */
public class FlightBooking implements Serializable {
    private static final long serialVersionUID = 134488688735435L;

    private Long id;

    private Long contactId;

    private Long hotelId;

    private Date bookingDate;


    public FlightBooking(Long contactId, Long hotelId, Date bookingDate) {
        this.contactId = contactId;
        this.hotelId = hotelId;
        this.bookingDate = bookingDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

}
