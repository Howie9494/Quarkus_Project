package uk.ac.newcastle.enterprisemiddleware.guestBooking;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author Howie
 * @Date 2022/11/9 16:17
 * @Version 1.0
 */
public class GuestBooking implements Serializable {
    private static final long serialVersionUID = 1347675646325L;
    
    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private Long hotelId;

    private Date bookingDate;

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.guestBooking.GuestBooking#getFirstName()
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.guestBooking.GuestBooking#setFirstName(String) 
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.guestBooking.GuestBooking#getLastName()
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.guestBooking.GuestBooking#setLastName(String) 
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.guestBooking.GuestBooking#getEmail()
     */
    public String getEmail() {
        return email;
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.guestBooking.GuestBooking#setEmail(String) 
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.guestBooking.GuestBooking#getPhoneNumber()
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.guestBooking.GuestBooking#setPhoneNumber(String) 
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.guestBooking.GuestBooking#getHotelId()
     */
    public Long getHotelId() {
        return hotelId;
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.guestBooking.GuestBooking#setHotelId(Long) 
     */
    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.guestBooking.GuestBooking#getBookingDate()
     */
    public Date getBookingDate() {
        return bookingDate;
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.guestBooking.GuestBooking#setBookingDate(Date) 
     */
    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }
}
