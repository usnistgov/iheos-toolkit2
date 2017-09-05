package gov.nist.toolkit.fhir.mhd.errors

import gov.nist.toolkit.fhir.mhd.ErrorLogger

/**
 *
 */
class ResourceNotAvailable extends AbstractError {

    ResourceNotAvailable(ErrorLogger errorLogger, String reference) {
        super(errorLogger)
        this.reference = reference
    }

}
