package uk.ac.newcastle.enterprisemiddleware.travelAgent;

import com.fasterxml.jackson.annotation.JsonIgnore;
import uk.ac.newcastle.enterprisemiddleware.booking.Booking;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * <p>this is a Domain object.<p/>
 * <p>The TravelAgentBooking class represents how TravelAgentBooking resources are represented in the application database.<p/>
 *
 * @author Howie
 */
@Entity
@NamedQueries({
        @NamedQuery(name = TravelAgentBooking.FIND_ALL,query = "SELECT t FROM TravelAgentBooking t ORDER BY t.id ASC")
})
@XmlRootElement
@Table(name = "travel_agent_booking")
public class TravelAgentBooking implements Serializable {

    private static final long serialVersionUID = 1365454634525L;

    public static final String FIND_ALL = "TravelAgentBooking.findAll";

    @Id
    @TableGenerator(name = "TRAVEL_AGENT_ID",table = "PK_TRAVEL_AGENT_GENERATE_TABLE")
    @GeneratedValue(strategy = GenerationType.TABLE,generator = "TRAVEL_AGENT_ID")
    private Long id;

    @Column(name = "hotel_customer_id")
    private Long hotelCustomerId;

    @Column(name = "taxi_customer_id")
    private Long taxiCustomerId;

    @Column(name = "flight_customer_id")
    private Long flightCustomerId;

    @Column(name = "hotel_booking_id")
    private Long hotelBookingId;

    @Column(name = "flight_booking_id")
    private Long flightBookingId;

    @Column(name = "taxi_booking_id")
    private Long taxiBookingId;

    @JsonIgnore
    @OneToOne(cascade = {CascadeType.PERSIST,CascadeType.REMOVE})
    private Booking booking;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHotelCustomerId() {
        return hotelCustomerId;
    }

    public void setHotelCustomerId(Long hotelCustomerId) {
        this.hotelCustomerId = hotelCustomerId;
    }

    public Long getTaxiCustomerId() {
        return taxiCustomerId;
    }

    public void setTaxiCustomerId(Long taxiCustomerId) {
        this.taxiCustomerId = taxiCustomerId;
    }

    public Long getFlightCustomerId() {
        return flightCustomerId;
    }

    public void setFlightCustomerId(Long flightCustomerId) {
        this.flightCustomerId = flightCustomerId;
    }

    public Long getHotelBookingId() {
        return hotelBookingId;
    }

    public void setHotelBookingId(Long hotelBookingId) {
        this.hotelBookingId = hotelBookingId;
    }

    public Long getFlightBookingId() {
        return flightBookingId;
    }

    public void setFlightBookingId(Long flightBookingId) {
        this.flightBookingId = flightBookingId;
    }

    public Long getTaxiBookingId() {
        return taxiBookingId;
    }

    public void setTaxiBookingId(Long taxiBookingId) {
        this.taxiBookingId = taxiBookingId;
    }
}
