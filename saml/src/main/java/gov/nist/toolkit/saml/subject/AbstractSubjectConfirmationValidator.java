package gov.nist.toolkit.saml.subject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.SubjectConfirmation;
import org.opensaml.saml2.core.SubjectConfirmationData;
import org.opensaml.xml.util.DatatypeHelper;
import org.opensaml.xml.validation.ValidationException;
public abstract class AbstractSubjectConfirmationValidator {
	/**
	     * The name of the {@link ValidationContext#getStaticParameters()} carrying a {@link Set<String>} whose values are
	     * the acceptable subject confirmation data recipient endpoints.
	     */
	    public static final String VALID_RECIPIENTS_PARAM = AbstractSubjectConfirmationValidator.class.getName()
	            + ".ValidRecipients";
	    
	    public static final String CLOCK_SKEW_PARAM  = AbstractSubjectConfirmationValidator.class.getName() + ".ClockSkew";
	
	    /** Default clock skew; {@value} milliseconds. */
	    public static final long DEFAULT_CLOCK_SKEW = 5 * 60 * 1000;
	    /**
	     * The name of the {@link ValidationContext#getStaticParameters()} carrying a {@link Set<InetAddress>} whose values
	     * are the acceptable subject confirmation data addresses.
	     */
	    public static final String VALID_ADDRESSES_PARAM = AbstractSubjectConfirmationValidator.class.getName()
	            + ".ValidAddresses";
	
	    /** Class logger. */
	   // private Logger log = LoggerFactory.getLogger(AbstractSubjectConfirmationValidator.class);
	
	    /** Constructor. */
	    public AbstractSubjectConfirmationValidator() {
	    }
	
	    /** {@inheritDoc} */
	    public ValidationResult validate(SubjectConfirmation confirmation, Assertion assertion, ValidationContext context)
	            throws ValidationException {
	
	        if (confirmation.getSubjectConfirmationData() != null) {
	            ValidationResult result = validateNotBefore(confirmation, assertion, context);
	            if (result != ValidationResult.VALID) {
	                return result;
	            }
	
	            result = validateNotOnOrAfter(confirmation, assertion, context);
	            if (result != ValidationResult.VALID) {
	                return result;
	            }
	
	            result = validateRecipient(confirmation, assertion, context);
	            if (result != ValidationResult.VALID) {
	                return result;
	            }
	
	            result = validateAddress(confirmation, assertion, context);
	            if (result != ValidationResult.VALID) {
	                return result;
	            }
	        }
	
	        return doValidate(confirmation, assertion, context);
	    }
	
	    /**
	     * Validates the <code>NotBefore</code> condition of the {@link SubjectConfirmationData}, if any is present.
	     * 
	     * @param confirmation confirmation method, with {@link SubjectConfirmationData}, being validated
	     * @param assertion assertion bearing the confirmation method
	     * @param context current validation context
	     * 
	     * @return the result of the validation evaluation
	     */
	    protected ValidationResult validateNotBefore(SubjectConfirmation confirmation, Assertion assertion,
	            ValidationContext context) {
	        DateTime skewedNow = new DateTime(ISOChronology.getInstanceUTC()).plus(getClockSkew(context));
	        DateTime notBefore = confirmation.getSubjectConfirmationData().getNotBefore();
	        
	       
	        if (notBefore != null && notBefore.isAfter(skewedNow)) {
	            context.setValidationFailureMessage(String.format(
	                    "Subject confirmation, in assertion '%s', with NotBefore condition of '%s' is not yet valid"+
	                    assertion.getID()+", "+ notBefore));
	            return ValidationResult.INVALID;
	        }
	
	        return ValidationResult.VALID;
	    }
	    
	    /**
	    * Gets the clock skew from the {@link ValidationContext#getStaticParameters()} parameters. If the parameter is not
	    * set or is not a positive {@link Long} then the {@link #DEFAULT_CLOCK_SKEW} is used.
	    * 
	    * @param context current validation context
	    * 
	    * @return the clock skew
	    */
	    public static long getClockSkew(ValidationContext context) {
	        long clockSkew = DEFAULT_CLOCK_SKEW;
	
	        if (context.getStaticParameters().containsKey(CLOCK_SKEW_PARAM)) {
	            try {
	                clockSkew = (Long) context.getStaticParameters().get(CLOCK_SKEW_PARAM);
	                if (clockSkew < 1) {
	                    clockSkew = DEFAULT_CLOCK_SKEW;
	                }
	            } catch (ClassCastException e) {
	                clockSkew = DEFAULT_CLOCK_SKEW;
	            }
	        }
	
	        return clockSkew;
	    }
	
	    /**
	     * Validates the <code>NotOnOrAfter</code> condition of the {@link SubjectConfirmationData}, if any is present.
	     * 
	     * @param confirmation confirmation method, with {@link SubjectConfirmationData}, being validated
	     * @param assertion assertion bearing the confirmation method
	     * @param context current validation context
	     * 
	     * @return the result of the validation evaluation
	     */
	    protected ValidationResult validateNotOnOrAfter(SubjectConfirmation confirmation, Assertion assertion,
	            ValidationContext context) {
	        DateTime skewedNow = new DateTime(ISOChronology.getInstanceUTC()).minus(getClockSkew(context));
	        DateTime notOnOrAfter = confirmation.getSubjectConfirmationData().getNotOnOrAfter();
	        
	       
	        if (notOnOrAfter != null && notOnOrAfter.isBefore(skewedNow)) {
	            context.setValidationFailureMessage(String.format(
	                    "Subject confirmation, in assertion '%s', with NotOnOrAfter condition of '%s' is no longer valid",
	                    assertion.getID(), notOnOrAfter));
	            return ValidationResult.INVALID;
	        }
	
	        return ValidationResult.VALID;
	    }
	
	    /**
	     * Validates the <code>Recipient</code> condition of the {@link SubjectConfirmationData}, if any is present.
	     * 
	     * @param confirmation confirmation method being validated
	     * @param assertion assertion bearing the confirmation method
	     * @param context current validation context
	     * 
	     * @return the result of the validation evaluation
	     */
	    protected ValidationResult validateRecipient(SubjectConfirmation confirmation, Assertion assertion,
	            ValidationContext context) {
	        String recipient = DatatypeHelper
	                .safeTrimOrNullString(confirmation.getSubjectConfirmationData().getRecipient());
	        if (recipient == null) {
	            return ValidationResult.VALID;
	        }
	        
	       	
	        Set<String> validRecipients;
	        try {
	            validRecipients = (Set<String>) context.getStaticParameters().get(VALID_RECIPIENTS_PARAM);
	        } catch (ClassCastException e) {
	            context.setValidationFailureMessage(
	                    "Unable to determine list of valid subject confirmation recipient endpoints");
	            return ValidationResult.INDETERMINATE;
	        }
	        if (validRecipients == null || validRecipients.isEmpty()) {
	            context.setValidationFailureMessage(
	                    "Unable to determine list of valid subject confirmation recipient endpoints");
	            return ValidationResult.INDETERMINATE;
	        }
	        
	        
	
	        if (validRecipients.contains(recipient)) {
	            return ValidationResult.VALID;
	        }
	        
	        context.setValidationFailureMessage(String.format(
	                "Subject confirmation recipient for asertion '%s' did not match any valid recipients", assertion
	                        .getID()));
	        return ValidationResult.INVALID;
	    }
	
	    /**
	     * Validates the <code>Address</code> condition of the {@link SubjectConfirmationData}, if any is present.
	     * 
	     * @param confirmation confirmation method being validated
	     * @param assertion assertion bearing the confirmation method
	     * @param context current validation context
	     * 
	     * @return the result of the validation evaluation
	     * 
	     * @throws ValidationException thrown if address of assertion sender does is not the address listed in the subject
	     *             confirmation data
	     */
	    protected ValidationResult validateAddress(SubjectConfirmation confirmation, Assertion assertion,
	            ValidationContext context) throws ValidationException {
	        String address = DatatypeHelper.safeTrimOrNullString(confirmation.getSubjectConfirmationData().getAddress());
	        if (address == null) {
	            return ValidationResult.VALID;
	        }
	        
	        InetAddress[] confirmingAddresses;
	        try {
	            confirmingAddresses = InetAddress.getAllByName(address);
	        } catch (UnknownHostException e) {
	            context.setValidationFailureMessage(String.format(
	                    "Subject confirmation address '%s' is not resolvable hostname or IP address", address));
	            return ValidationResult.INDETERMINATE;
	        }
	        
	       
	
	        Set<InetAddress> validAddresses;
	        try {
	            validAddresses = (Set<InetAddress>) context.getStaticParameters().get(VALID_ADDRESSES_PARAM);
	        } catch (ClassCastException e) {
	            context.setValidationFailureMessage("Unable to determine list of valid subject confirmation addresses");
	            return ValidationResult.INDETERMINATE;
	        }
	        if (validAddresses == null || validAddresses.isEmpty()) {
	            context.setValidationFailureMessage("Unable to determine list of valid subject confirmation addresses");
	            return ValidationResult.INDETERMINATE;
	        }
	
	        for (InetAddress confirmingAddress : confirmingAddresses) {
	            if (validAddresses.contains(confirmingAddress)) {
	                return ValidationResult.VALID;
	            }
	        }
	        
	        context.setValidationFailureMessage(String.format(
	                "Subject confirmation address for asertion '%s' did not match any valid addresses", assertion
	                        .getID()));
	        return ValidationResult.INVALID;
	    }
	
	    /**
	     * Performs any further validation required for the specific confirmation method implementation.
	     * 
	     * @param confirmation confirmation method being validated
	     * @param assertion assertion bearing the confirmation method
	     * @param context current validation context
	     * 
	     * @return the result of the validation evaluation
	     * 
	     * @throws ValidationException thrown if further validation finds the confirmation method to be invalid
	     */
	    protected abstract ValidationResult doValidate(SubjectConfirmation confirmation, Assertion assertion,
	            ValidationContext context) throws ValidationException;
}
