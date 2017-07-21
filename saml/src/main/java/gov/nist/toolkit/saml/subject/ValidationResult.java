package gov.nist.toolkit.saml.subject;
import net.jcip.annotations.ThreadSafe;
@ThreadSafe
public enum ValidationResult {
	/** Indicates that the assertion passed validation and should be considered valid. */
    VALID,

    /** Indicates that the validity of the assertion could not be determined. */
    INDETERMINATE,

    /** Indicates that the assertion failed validation, should be considered invalid. */
    INVALID
}
