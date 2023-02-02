package uk.ac.newcastle.enterprisemiddleware.booking;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Range;
import uk.ac.newcastle.enterprisemiddleware.travelAgent.TravelAgentBooking;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>this is a Domain object.<p/>
 * <p>The Booking class represents how Booking resources are represented in the application database.<p/>
 *
 * @author Howie
 */
@Entity
@NamedQueries({
        @NamedQuery(name = Booking.FIND_ALL,query = "SELECT b FROM Booking b ORDER BY b.id ASC"),
        @NamedQuery(name = Booking.FIND_BY_HOTEL_AND_DATE,query = "SELECT b FROM Booking b WHERE b.hotelId = :hotelId AND b.bookingDate = :bookingDate")
})
@XmlRootElement
@Table(name = "booking",uniqueConstraints = @UniqueConstraint(columnNames = {"hotel_id","booking_date"}))
public class Booking implements Serializable {
    private static final long serialVersionUID = 1347673944325L;

    public static final String FIND_ALL = "Booking.findAll";
    public static final String FIND_BY_HOTEL_AND_DATE = "Booking.findByHotelAndDate";

    @Id
    @TableGenerator(name = "BOOKING_ID",table = "PK_BOOKING_GENERATE_TABLE")
    @GeneratedValue(strategy = GenerationType.TABLE,generator = "BOOKING_ID")
    private Long id;

    @NotNull
    @Column(name = "hotel_id")
    private Long hotelId;

    @NotNull
    @Column(name = "customer_id")
    private Long customerId;

    @NotNull
    @Future(message = "booking date can not be in the past. Please choose one from the future")
    @Temporal(TemporalType.DATE)
    @Column(name = "booking_date")
    private Date bookingDate;

    @JsonIgnore
    @OneToOne(mappedBy = "booking",cascade = CascadeType.PERSIST)
    private TravelAgentBooking travelAgentBooking;

    public Booking() {
    }

    public Booking(@NotNull Long hotelId,
                   @NotNull Long customerId,
                   @NotNull @Future(message = "booking date can not be in the past. Please choose one from the future") Date bookingDate) {
        this.hotelId = hotelId;
        this.customerId = customerId;
        this.bookingDate = bookingDate;
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.booking.Booking#getId()
     */
    public Long getId() {
        return id;
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.booking.Booking#setId(Long)
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.booking.Booking#getHotelId()
     */
    public Long getHotelId() {
        return hotelId;
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.booking.Booking#setHotelId(Long)
     */
    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.booking.Booking#getCustomerId()
     */
    public Long getCustomerId() {
        return customerId;
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.booking.Booking#setCustomerId(Long)
     */
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.booking.Booking#getBookingDate()
     */
    public Date getBookingDate() {
        return bookingDate;
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.booking.Booking#setBookingDate(Date)
     */
    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.booking.Booking#equals(Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking)) return false;
        Booking booking = (Booking) o;
        return hotelId.equals(booking.hotelId) && bookingDate.equals(booking.bookingDate);
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.booking.Booking#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = 17;
        hash = 31 * hash + hotelId.hashCode();
        hash = 31 * hash + bookingDate.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", hotelId=" + hotelId +
                ", customerId=" + customerId +
                ", bookingDate=" + bookingDate +
                '}';
    }
}
