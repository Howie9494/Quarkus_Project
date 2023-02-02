package uk.ac.newcastle.enterprisemiddleware.taxi;

import java.io.Serializable;
import java.util.Date;

/**
 * {
 *   "id": 0,
 *   "taxiId": 0,
 *   "customerId": 0,
 *   "bookingDate": "2022-11-14"
 * }
 */
public class TaxiBooking implements Serializable {

    private static final long serialVersionUID = 1344255474535435L;

    private Long id;

    private Long taxiId;

    private Long customerId;

    private Date bookingDate;

    public TaxiBooking() {
    }

    public TaxiBooking(Long taxiId, Long customerId, Date bookingDate) {
        this.taxiId = taxiId;
        this.customerId = customerId;
        this.bookingDate = bookingDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaxiId() {
        return taxiId;
    }

    public void setTaxiId(Long taxiId) {
        this.taxiId = taxiId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }
}
