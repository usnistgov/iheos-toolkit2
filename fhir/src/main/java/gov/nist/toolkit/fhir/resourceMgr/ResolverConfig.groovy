package gov.nist.toolkit.fhir.resourceMgr
/**
 * used to configure ResourceMgr
 */
class ResolverConfig {
    boolean relativeReferenceOk = true
    boolean relativeReferenceRequired = false

    boolean externalRequired = false
    boolean internalRequired = false

    boolean containedOk = false
    boolean containedRequired = false

    ResolverConfig noRelative() {
        relativeReferenceOk = false
        this
    }

    ResolverConfig relativeRequired() {
        relativeReferenceRequired = true
        this
    }

    ResolverConfig externalRequired() {
        externalRequired = true
        this
    }

    ResolverConfig internalRequired() {
        internalRequired = true
        this
    }

    ResolverConfig containedOk() {
        containedOk = true
        this
    }

    ResolverConfig containedRequired() {
        containedRequired = true
        containedOk = true
        this
    }

    String toString() {
        ((!relativeReferenceOk) ? " relativeNotAllowed" : '') +
                ((relativeReferenceRequired) ? " relativeRequired" : '') +
                ((externalRequired) ? " externalRequired" : '') +
                ((internalRequired) ? " internalRequired" : '') +
                ((containedOk) ? " containedOk" : '') +
                ((containedRequired) ? " containedRequired" : '')
    }

}
