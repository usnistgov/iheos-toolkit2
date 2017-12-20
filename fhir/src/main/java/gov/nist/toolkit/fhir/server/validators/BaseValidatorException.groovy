package gov.nist.toolkit.fhir.server.validators

/**
 *
 */
class BaseValidatorException extends Exception {
    String diagnostic
    String spec

    BaseValidatorException(String diagnostic, String spec) {
        this.diagnostic = diagnostic
        this.spec = spec
    }
}
