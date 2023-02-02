package uk.ac.newcastle.enterprisemiddleware.customer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>This class provides methods to check Customer objects against arbitrary requirements.</p>
 *
 * @author Howie
 * @see Customer
 * @see CustomerRepository
 * @see javax.validation.Validator
 */
@ApplicationScoped
public class CustomerValidator {

    @Inject
    Validator validator;

    @Inject
    CustomerRepository crud;

    /**
     * <p>Validates the given Customer object and throws validation exceptions based on the type of error. If the error is standard
     * bean validation errors then it will throw a ConstraintValidationException with the set of the constraints violated.<p/>
     *
     *
     * <p>If the error is caused because an existing customer with the same email is registered it throws a regular validation
     * exception so that it can be interpreted separately.</p>
     *
     *
     * @param customer The Customer object to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws ValidationException If Customer with the same email already exists
     */
    void validateCustomer(Customer customer) throws ConstraintViolationException, ValidationException, UniqueEmailException {
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }

        if (emailAlreadyExists(customer.getEmail(), customer.getId())) {
            throw new UniqueEmailException("Unique Email Violation");
        }
    }

    /**
     * <p>Checks if a Customer with the same email address is already registered. This is the only way to easily capture the
     * "@UniqueConstraint(columnNames = "email")" constraint from the Customer class.</p>
     *
     * <p>Since Update will being using an email that is already in the database we need to make sure that it is the email
     * from the record being updated.</p>
     *
     * @param email The email to check is unique
     * @param id The user id to check the email against if it was found
     * @return boolean which represents whether the email was found, and if so if it belongs to the user with id
     */
    boolean emailAlreadyExists(String email, Long id) {
        Customer customer = null;
        Customer CustomerWithID = null;
        try {
            customer = crud.findByEmail(email);
        } catch (NoResultException e) {
            // ignore
        }

        if (customer != null && id != null) {
            try {
                CustomerWithID = crud.findById(id);
                if (CustomerWithID != null && CustomerWithID.getEmail().equals(email)) {
                    customer = null;
                }
            } catch (NoResultException e) {
                // ignore
            }
        }
        return customer != null;
    }

}
