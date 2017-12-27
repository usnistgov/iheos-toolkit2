package gov.nist.toolkit.simcoresupport.mhd.errors

import gov.nist.toolkit.simcoresupport.mhd.ErrorLogger
import gov.nist.toolkit.simcoresupport.mhd.MhdGenerator

class ResourceTypeNotAllowedInPDB extends AbstractError {
    Class resourceClass

    ResourceTypeNotAllowedInPDB(ErrorLogger errorLogger, Class resourceClass) {
        super(errorLogger)
        this.resourceClass = resourceClass
    }

    @Override
    String toString() {
        "Resource of type ${resourceClass.simpleName} is not allowed in ITI-65 Provide Document Bundle transaction\n" +
                "...only ${MhdGenerator.acceptableResourceTypes} are allowed."
    }
}
