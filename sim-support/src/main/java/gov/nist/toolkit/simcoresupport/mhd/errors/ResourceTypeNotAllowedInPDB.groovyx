package gov.nist.toolkit.simcoresupport.mhd.errors

import gov.nist.toolkit.simcoresupport.mhd.ErrorLogger
import gov.nist.toolkit.simcoresupport.mhd.MhdGenerator

class ResourceTypeNotAllowedInPDB extends AbstractError {
    String resourceClassName

    ResourceTypeNotAllowedInPDB(ErrorLogger errorLogger, String resourceClassName) {
        super(errorLogger)
        this.resourceClassName = resourceClassName
    }

    @Override
    String toString() {
        "Resource of type ${resourceClassName} is not allowed in ITI-65 Provide Document Bundle transaction\n" +
                "...only ${MhdGenerator.acceptableResourceTypes.collect { it.simpleName }} are allowed."
    }
}
