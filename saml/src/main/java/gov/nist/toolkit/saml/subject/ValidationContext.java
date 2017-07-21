package gov.nist.toolkit.saml.subject;

import java.util.Collections;
import java.util.Map;
	
import org.opensaml.xml.util.DatatypeHelper;
import org.opensaml.xml.util.LazyMap;
public class ValidationContext {
	/** Static parameters used as input to the validation process. */
		    private Map<String, Object> staticParameters;
		
		    /** Dynamic parameters used input to, and output from, the validation process. */
		    private Map<String, Object> dynamicParameters;
		
		    /** Error messaging describing what validation check an assertion failed. */
		    private String validationFailureMessage;
		
		    /** Constructor. Creates a validation context with no global environment. */
		    public ValidationContext() {
		        this(null);
		    }
		
		    /**
		     * Constructor.
		     * 
		     * @param newStaticParameters static parameters for the validation evaluation
		     */
		    public ValidationContext(Map<String, Object> newStaticParameters) {
		        if (newStaticParameters == null) {
		            staticParameters = Collections.unmodifiableMap(Collections.EMPTY_MAP);
		        } else {
		            staticParameters = Collections.unmodifiableMap(newStaticParameters);
		        }
		        dynamicParameters = new LazyMap<String, Object>();
		    }
		
		    /**
		     * Gets the static parameters used as input to the validation process. The returned map is immutable.
		     * 
		     * @return static parameters used as input to the validation process
		     */
		    public Map<String, Object> getStaticParameters() {
		        return staticParameters;
		    }
		
		    /**
		     * Gets the dynamic parameters used input to, and output from, the validation process. The returned map is mutable.
		     * 
		     * @return dynamic parameters used input to, and output from, the validation process
		     */
		    public Map<String, Object> getDynamicParameters() {
		        return dynamicParameters;
		    }
		
		    /**
		     * Gets the message describing why the validation process failed.
		     * 
		     * @return message describing why the validation process failed
		     */
		    public String getValidationFailureMessage() {
		        return validationFailureMessage;
		    }
		
		    /**
		     * Sets the message describing why the validation process failed.
		     * 
		     * @param message message describing why the validation process failed
		     */
		    public void setValidationFailureMessage(String message) {
		        validationFailureMessage = DatatypeHelper.safeTrimOrNullString(message);
		    }
}
