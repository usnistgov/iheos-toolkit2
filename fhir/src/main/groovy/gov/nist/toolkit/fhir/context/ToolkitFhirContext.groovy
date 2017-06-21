package gov.nist.toolkit.fhir.context

import ca.uhn.fhir.context.FhirContext

/**
 * Each time we buil the FHiR Context it indexed the resource
 * definitions.  Very costly. This copy of the context should be
 * refernced everywhere.
 */
class ToolkitFhirContext {
    static private FhirContext ourCtx

    public static FhirContext get() {
        if (!ourCtx)
            ourCtx = FhirContext.forDstu3()
        return ourCtx
    }
}
