package gov.nist.toolkit.fhir.mhd.errors

import gov.nist.toolkit.fhir.mhd.ErrorLogger
import gov.nist.toolkit.fhir.mhd.ResourceMgr

/**
 *
 */
class ResourceNotAvailable extends AbstractError {
    String referencedUrl
    String referencedResourceType
    String referencingUrl

    ResourceNotAvailable(ErrorLogger errorLogger, String referencingObjectUrl, String referencedUrl) {
        super(errorLogger)
        this.referencedUrl = referencedUrl
        this.referencingUrl = referencingObjectUrl
        if (referencedUrl)
            referencedResourceType = ResourceMgr.resourceTypeFromUrl(referencedUrl)
    }

    String toString() {
        "${referencedResourceType} reference in ${referencingUrl} : ${referencedUrl} cannot be resolved"
    }

}
