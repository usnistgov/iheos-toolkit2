package gov.nist.toolkit.fhir.simulators.mhd.errors

import gov.nist.toolkit.fhir.resourceMgr.ResourceMgr
import gov.nist.toolkit.fhir.simulators.mhd.ErrorLogger

/**
 *
 */
class ResourceNotAvailable extends AbstractError {
    String referencedUrl
    String referencedResourceType
    String referencingUrl
    String extra
    String specref

    ResourceNotAvailable(ErrorLogger errorLogger, referencingObjectUrl, referencedUrl) {
        this(errorLogger, referencingObjectUrl, referencedUrl, null, null)
    }

    ResourceNotAvailable(ErrorLogger errorLogger, referencingObjectUrl, referencedUrl, extra, specref) {
        super(errorLogger)
        this.referencedUrl = referencedUrl
        this.referencingUrl = referencingObjectUrl
        this.extra = extra
        this.specref = specref
        if (referencedUrl)
            referencedResourceType = ResourceMgr.resourceTypeFromUrl(referencedUrl)
    }

    String toString() {
        StringBuilder buf = new StringBuilder()
        buf.append("${referencedResourceType} reference in ${referencingUrl} : ${referencedUrl} cannot be resolved")
        if (extra)
            buf.append('\n').append(extra)
        if (specref)
            buf.append('\nSee: ').append(specref)

        return buf.toString()
    }

}
