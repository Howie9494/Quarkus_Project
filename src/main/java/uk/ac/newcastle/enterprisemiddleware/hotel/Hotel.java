package uk.ac.newcastle.enterprisemiddleware.hotel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import uk.ac.newcastle.enterprisemiddleware.booking.Booking;

import javax.persistence.*;
import javax.validation.constraints.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

/**
 * <p>this is a Domain object.<p/>
 * <p>The Hotel class represents how Hotel resources are represented in the application database.<p/>
 *
 * @author Howie
 */
@Entity
@NamedQueries({
        @NamedQuery(name = Hotel.FIND_ALL,query = "SELECT h FROM Hotel h ORDER BY h.id ASC"),
        @NamedQuery(name = Hotel.FIND_BY_PHONE_NUMBER,query = "SELECT h FROM Hotel h WHERE h.phoneNumber = :phoneNumber")
})
@XmlRootElement
@Table(name = "hotel",uniqueConstraints = @UniqueConstraint(columnNames = "phone_number"))
public class Hotel implements Serializable {

    private static final long serialVersionUID = 1347673944325L;

    public static final String FIND_ALL = "Hotel.findAll";
    public static final String FIND_BY_PHONE_NUMBER = "Hotel.findByPhoneNumber";

    @Id
    @TableGenerator(name = "HOTEL_ID",table = "PK_HOTEL_GENERATE_TABLE")
    @GeneratedValue(strategy = GenerationType.TABLE,generator = "HOTEL_ID")
    private Long id;

    @NotNull
    @NotEmpty
    @Size(min = 1,max = 50)
    @Column(name = "hotel_name")
    private String hotelName;

    @NotNull
    @NotEmpty
    @Pattern(regexp = "^[0-9A-Z]{6}$")
    private String postcode;

    @NotNull
    @NotEmpty
    @Pattern(regexp = "^\\(0[2-9][0-8][0-9]\\)\\s?[0-9]{3}\\-[0-9]{4}$")
    @Column(name = "phone_number")
    private String phoneNumber;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "hotel_id")
    private List<Booking> bookingList;

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.hotel.Hotel#getId()
     */
    public Long getId() {
        return id;
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.hotel.Hotel#setId(Long)
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.hotel.Hotel#getHotelName()
     */
    public String getHotelName() {
        return hotelName;
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.hotel.Hotel#setHotelName(String)
     */
    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.hotel.Hotel#getPostcode()
     */
    public String getPostcode() {
        return postcode;
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.hotel.Hotel#setPostcode(String) 
     */
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.hotel.Hotel#getPhoneNumber()
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.hotel.Hotel#setPhoneNumber(String) 
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.hotel.Hotel#equals(Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Hotel)) return false;
        Hotel hotel = (Hotel) o;
        return phoneNumber.equals(hotel.phoneNumber);
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.hotel.Hotel#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = 17;
        hash = 31 * hash + phoneNumber.hashCode();
        return hash;
    }
}
