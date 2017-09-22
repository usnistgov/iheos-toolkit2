package gov.nist.toolkit.simulators.fhir.validators

/**
 *
 */
class MismatchException extends BaseValidatorException {
    String expectedValue
    String foundValue
    String expectedValueSource
    String foundValueSource

    MismatchException(String diagnostic, String spec, String expectedValue, String foundValue, String expectedValueSource, String foundValueSource) {
        super(diagnostic, spec)
        this.expectedValue = expectedValue
        this.foundValue = foundValue
        this.expectedValueSource = expectedValueSource
        this.foundValueSource = foundValueSource
    }
}
