package gov.nist.toolkit.simProxy.server.transforms

import ca.uhn.fhir.context.FhirContext
import gov.nist.toolkit.actortransaction.server.AbstractProxyTransform
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.fhir.simulators.mhd.Attachment
import gov.nist.toolkit.fhir.simulators.mhd.MhdGenerator
import gov.nist.toolkit.fhir.simulators.mhd.Submission
import gov.nist.toolkit.http.HttpParser
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.fhir.resourceMgr.ResourceCache
import gov.nist.toolkit.fhir.simulators.proxy.util.PartSpec
import gov.nist.toolkit.fhir.simulators.proxy.util.SoapBuilder
/**
 * for translating MHD ITI-65 to XDS PnR
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
            Submission s = new MhdGenerator(Installation.instance().resourceCacheMgr()).buildSubmission(bundle)
            assert s.attachments.size() > 0
            List<PartSpec> parts = []
            parts << new PartSpec('application/xop+xml; charset=UTF-8; type="application/soap+xml"', s.metadataInSoapWrapper())
            s.attachments.each { Attachment a ->
                parts << new PartSpec(a.contentType, new String(a.content))
            }

            def (header, body1) = new SoapBuilder().mtomSoap('/theservice', 'localhost', '8080', 'noaction', parts)
            this.outputHeader = header
            this.outputBody = body1
        }
        return TransactionType.PROVIDE_AND_REGISTER
    }
}
