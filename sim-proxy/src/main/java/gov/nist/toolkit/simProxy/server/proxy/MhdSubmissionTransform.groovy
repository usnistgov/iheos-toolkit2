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

        String body = new String(inputBody)

        buf.append(inputHeader.trim()).append('\n\n').append(body)

        HttpParser parser = new HttpParser(buf.toString().bytes)

        String contentType = parser.getHeaderValue('Content-Type')

        if (contentType.indexOf('application/fhir') != -1) {
            def bundle = null
            if (contentType.contains('+xml'))
                bundle = ctx.newXmlParser().parseResource(body)
            else if (contentType.contains('+json'))
                bundle = ctx.newJsonParser().parseResource(body)
            assert bundle
            Submission s = new MhdGenerator(ResourceCacheFactory.resourceCacheMgr).buildSubmission(bundle)
            assert s.attachments.size() > 0
        }
        return TransactionType.FHIR
    }
}
