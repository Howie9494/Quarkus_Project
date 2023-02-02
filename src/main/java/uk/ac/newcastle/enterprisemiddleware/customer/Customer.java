package uk.ac.newcastle.enterprisemiddleware.customer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import uk.ac.newcastle.enterprisemiddleware.booking.Booking;

import javax.persistence.*;
import javax.validation.constraints.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

/**
 * <p>this is a Domain object.<p/>
 * <p>The Customer class represents how customer resources are represented in the application database.<p/>
 *
 * @author Howie
 */

@Entity
@NamedQueries({
        @NamedQuery(name = Customer.FIND_ALL, query = "SELECT c FROM Customer c ORDER BY c.id ASC"),
        @NamedQuery(name = Customer.FIND_BY_EMAIL, query = "SELECT c FROM Customer c WHERE c.email = :email"),
})
@XmlRootElement
@Table(name = "customer", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Customer implements Serializable {

    private static final long serialVersionUID = 13423525325134325L;

    public static final String FIND_ALL = "Customer.findAll";
    public static final String FIND_BY_EMAIL = "Customer.findByEmail";

    @Id
    @TableGenerator(name = "CUSTOMER_ID",table = "PK_CUSTOMER_GENERATE_TABLE")
    @GeneratedValue(strategy = GenerationType.TABLE,generator = "CUSTOMER_ID")
    private Long id;

    @NotNull
    @NotEmpty
    @Size(min = 1, max = 25)
    @Pattern(regexp = "[A-Za-z-']+", message = "Please use a name without numbers or specials")
    @Column(name = "first_name")
    private String firstName;

    @NotNull
    @NotEmpty
    @Size(min = 1, max = 25)
    @Pattern(regexp = "[A-Za-z-']+", message = "Please use a name without numbers or specials")
    @Column(name = "last_name")
    private String lastName;

    @NotNull
    @NotEmpty
    @Email(message = "The email address must be in the format of name@domain.com")
    private String email;

    @NotNull
    @NotEmpty
    @Pattern(regexp = "^\\(0[2-9][0-8][0-9]\\)\\s?[0-9]{3}\\-[0-9]{4}$")
    @Column(name = "phone_number")
    private String phoneNumber;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "customer_id")
    private List<Booking> bookingList;

    public Customer() {
    }

    public Customer(@NotNull @NotEmpty @Size(min = 1, max = 25) @Pattern(regexp = "[A-Za-z-']+", message = "Please use a name without numbers or specials") String firstName,
                    @NotNull @NotEmpty @Size(min = 1, max = 25) @Pattern(regexp = "[A-Za-z-']+", message = "Please use a name without numbers or specials") String lastName,
                    @NotNull @NotEmpty @Email(message = "The email address must be in the format of name@domain.com") String email,
                    @NotNull @NotEmpty @Pattern(regexp = "^\\(0[2-9][0-8][0-9]\\)\\s?[0-9]{3}\\-[0-9]{4}$") String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.customer.Customer#getId()
     */
    public Long getId() {
        return id;
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.customer.Customer#setId(Long)
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.customer.Customer#getFirstName()
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.customer.Customer#setFirstName(String)
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.customer.Customer#getLastName()
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.customer.Customer#setLastName(String)
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.customer.Customer#getEmail()
     */
    public String getEmail() {
        return email;
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.customer.Customer#setEmail(String)
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.customer.Customer#getPhoneNumber()
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.customer.Customer#setPhoneNumber(String)
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.customer.Customer#equals(Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        Customer customer = (Customer) o;
        return email.equals(customer.email);
    }

    /**
     * @see uk.ac.newcastle.enterprisemiddleware.customer.Customer#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = 17;
        hash = 31 * hash + email.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", bookingList=" + bookingList +
                '}';
    }
}
