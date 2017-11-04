package gov.nist.toolkit.simProxy.server.transforms

import ca.uhn.fhir.context.FhirContext
import gov.nist.toolkit.actortransaction.server.AbstractProxyTransform
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.fhir.resourceMgr.ResourceCache

/**
 * for translating XDS PnR response to MHD ITI-65
 */
class MhdSubmissionResponseTransformation extends AbstractProxyTransform {
    @Override
    TransactionType run() {
        FhirContext ctx = ResourceCache.ctx

        return null
    }
}
