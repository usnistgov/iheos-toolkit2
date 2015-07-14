package gov.nist.toolkit.valsupport.client;

/**
 * Created by bill on 7/8/15.
 */
public class EmptyValidationContextFactory {

    static public ValidationContext validationContext() {
        return new ValidationContext(null);
    }
}
