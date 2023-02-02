package uk.ac.newcastle.enterprisemiddleware.travelAgent;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author Howie
 * @Date 2022/11/15 10:49
 * @Version 1.0
 */
public class TravelAgent implements Serializable {

    private static final long serialVersionUID = 1365465444325L;

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private Date birthDate;

    private Long hotelId;

    private Date hotelBookingDate;

    private Long flightId;

    private Date flightBookingDate;

    private Long taxiId;

    private Date taxiBookingDate;

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

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public Date getHotelBookingDate() {
        return hotelBookingDate;
    }

    public void setHotelBookingDate(Date hotelBookingDate) {
        this.hotelBookingDate = hotelBookingDate;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public Date getFlightBookingDate() {
        return flightBookingDate;
    }

    public void setFlightBookingDate(Date flightBookingDate) {
        this.flightBookingDate = flightBookingDate;
    }

    public Long getTaxiId() {
        return taxiId;
    }

    public void setTaxiId(Long taxiId) {
        this.taxiId = taxiId;
    }

    public Date getTaxiBookingDate() {
        return taxiBookingDate;
    }

    public void setTaxiBookingDate(Date taxiBookingDate) {
        this.taxiBookingDate = taxiBookingDate;
    }
}
