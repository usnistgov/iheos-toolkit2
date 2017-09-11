package gov.nist.toolkit.simProxy.server.proxy

import ca.uhn.fhir.context.FhirContext
import gov.nist.toolkit.actortransaction.server.AbstractProxyTransform
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.fhir.mhd.MhdGenerator
import gov.nist.toolkit.fhir.mhd.ResourceCache
import gov.nist.toolkit.fhir.mhd.ResourceCacheFactory
import gov.nist.toolkit.fhir.mhd.Submission
import gov.nist.toolkit.http.HttpParser

/**
 *
 */
class MhdSubmissionTransform extends AbstractProxyTransform {
    @Override
    TransactionType run() {
        FhirContext ctx = ResourceCache.ctx
        StringBuilder buf = new StringBuilder()

        buf.append(inputHeader.trim()).append('\n\n').append(new String(inputBody))

        HttpParser parser = new HttpParser(buf.toString().bytes)

        if (parser.isMultipart()) {

        }

        String contentType = parser.getHeaderValue('Content-Type')

        if (contentType.indexOf('xml') != -1) {
            def bundle = ctx.newXmlParser().parseResource(inputBody)
            Submission s = new MhdGenerator(ResourceCacheFactory.resourceCacheMgr).buildSubmission(bundle)
            assert s.attachments.size() > 0
        }
        return TransactionType.FHIR
    }
}
