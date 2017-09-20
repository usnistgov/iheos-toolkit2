package gov.nist.toolkit.simulators.proxy.transforms

import ca.uhn.fhir.context.FhirContext
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.simulators.mhd.Attachment
import gov.nist.toolkit.simulators.mhd.MhdGenerator
import gov.nist.toolkit.simulators.mhd.Submission
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.installation.ResourceCache
import gov.nist.toolkit.simulators.proxy.exceptions.SimProxyTransformException
import gov.nist.toolkit.simulators.proxy.util.*
import gov.nist.toolkit.utilities.io.Io
import org.apache.http.Header
import org.apache.http.HttpEntity
import org.apache.http.HttpRequest
import org.apache.http.entity.BasicHttpEntity
import org.apache.http.message.BasicHttpEntityEnclosingRequest
import org.apache.log4j.Logger
import org.hl7.fhir.dstu3.model.Bundle
import org.hl7.fhir.instance.model.api.IBaseResource
/**
 *
 */
class MhdToPnrContentTransform implements ContentRequestTransform {
    static private final Logger logger = Logger.getLogger(MhdToPnrContentTransform.class);

    @Override
    HttpRequest run(SimProxyBase base, BasicHttpEntityEnclosingRequest request) {
        logger.info('Running MhdToPnrContentTransform')
        String contentType = request.allHeaders.find { Header h -> h.name.equalsIgnoreCase('content-type') }.value

        if (!contentType.startsWith('application/fhir'))
            throw new SimProxyTransformException("Content-Type is ${contentType} - expected application/fhir")

        byte[] clientContent = base.clientLogger.content
        assert clientContent, "Content is null"

        FhirContext ctx = ResourceCache.ctx

        IBaseResource resource = null
        if (contentType.contains('+xml'))
            resource = ctx.newXmlParser().parseResource(new String(clientContent))
        else if (contentType.contains('+json'))
            resource = ctx.newJsonParser().parseResource(new String(clientContent))
        assert resource instanceof Bundle
        Bundle bundle = resource
        Submission s = new MhdGenerator(Installation.instance().resourceCacheMgr()).buildSubmission(bundle)
        assert s.attachments.size() > 0
        List<PartSpec> parts = []
        parts << new PartSpec('application/xop+xml; charset=UTF-8; type="application/soap+xml"', s.metadata)
        s.attachments.each { Attachment a ->
            parts << new PartSpec(a.contentType, new String(a.content))
        }

        Header targetContentTypeHeader = MtomContentTypeGenerator.buildHeader(TransactionType.PROVIDE_AND_REGISTER.requestAction)
        request.setHeader(targetContentTypeHeader)
        BasicHttpEntity entity = new BasicHttpEntity()
        byte[] body = MtomContentTypeGenerator.buildBody(parts)
        base.targetLogger.logRequest(request)
        base.targetLogger.logRequestEntity(body)
        entity.setContent(Io.bytesToInputStream(body))
        request.setEntity(entity)

//        def (header, body1) = new SoapBuilder().mtomSoap('/theservice', 'localhost', '8080', 'noaction', parts)
//        this.outputHeader = header
//        this.outputBody = body1



        return request
    }

    @Override
    HttpRequest run(SimProxyBase base, HttpRequest request) {
        throw new SimProxyTransformException("MhdToPnrContentTransform cannot handle requests of type ${request.getClass().getName() } ")
    }
}
