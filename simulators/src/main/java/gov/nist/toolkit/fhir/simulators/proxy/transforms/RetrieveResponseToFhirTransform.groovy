package gov.nist.toolkit.fhir.simulators.proxy.transforms

import ca.uhn.fhir.context.FhirContext
import gov.nist.toolkit.fhir.resourceMgr.ResourceCache
import gov.nist.toolkit.fhir.simulators.proxy.util.ContentResponseTransform
import gov.nist.toolkit.fhir.simulators.proxy.util.RetrieveResponseParser
import gov.nist.toolkit.fhir.utility.WrapResourceInHttpResponse
import gov.nist.toolkit.simcoresupport.proxy.exceptions.SimProxyTransformException
import gov.nist.toolkit.simcoresupport.proxy.util.SimProxyBase
import gov.nist.toolkit.testengine.fhir.FhirSupport
import gov.nist.toolkit.utilities.io.Io
import org.apache.commons.httpclient.HttpStatus
import org.apache.http.Header
import org.apache.http.HttpResponse
import org.apache.http.ProtocolVersion
import org.apache.http.entity.ByteArrayEntity
import org.apache.http.message.BasicHttpResponse
import org.apache.http.message.BasicStatusLine
import org.apache.http.protocol.HttpDateGenerator
import org.apache.log4j.Logger
import org.hl7.fhir.dstu3.model.Binary
import org.hl7.fhir.dstu3.model.CodeType
import org.hl7.fhir.dstu3.model.OperationOutcome

class RetrieveResponseToFhirTransform implements ContentResponseTransform {
    static private final Logger logger = Logger.getLogger(RetrieveResponseToFhirTransform.class);
    @Override
    HttpResponse run(SimProxyBase base, BasicHttpResponse response) {
        FhirContext ctx = ResourceCache.ctx
        try {
            logger.info('Running RetrieveResponseToFhirTransform')
            String xmlBody
            Header contentTypeHeader = response.getHeaders('Content-Type')[0]
            if (!contentTypeHeader.value.startsWith('multipart'))
                throw new SimProxyTransformException('Not Implemented')
            String partContent = Io.getStringFromInputStream(response.getEntity().content)
            List<RetrieveResponseParser.RetrieveContent> contents = new RetrieveResponseParser().parse(partContent)
            if (contents.size() == 0) {
                response.statusCode = HttpStatus.SC_NOT_FOUND
                return response
            }
            if (contents.size() > 1) {
                response.statusCode = HttpStatus.SC_INTERNAL_SERVER_ERROR
                response.reasonPhrase = 'Multiple Documents found'
                return response
            }

            if (base.nonTradionalContentTypeRequested) {
                String contentType = base.chooseContentType()
                boolean wildcard = base.requestedContentTypeWildcarded
                String mimeType = contents[0].mimeType
                if (wildcard || mimeType == contentType) {
                    BasicHttpResponse outcome = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion('HTTP', 1, 1), HttpStatus.SC_OK, ''))
                    outcome.addHeader('Content-Type', mimeType)
                    outcome.addHeader('Date', new HttpDateGenerator().currentDate)
                    outcome.setEntity(new ByteArrayEntity(contents[0].content))
                    return outcome
                }
            }

            Binary binary = new Binary()
            binary.contentTypeElement = new CodeType(contents[0].mimeType)
            binary.setContent(contents[0].content)
            binary.id = contents[0].documentUniqueId

            return WrapResourceInHttpResponse.wrap(base.chooseContentType(), binary, HttpStatus.SC_OK)


        } catch (Throwable e) {
            OperationOutcome oo = FhirSupport.operationOutcomeFromThrowable(e)
//            OperationOutcome.OperationOutcomeIssueComponent com = new OperationOutcome.OperationOutcomeIssueComponent()
//            com.setSeverity(OperationOutcome.IssueSeverity.FATAL)
//            com.setCode(OperationOutcome.IssueType.EXCEPTION)
//            com.setDiagnostics(ExceptionUtil.exception_details(e))
//            oo.addIssue(com)
            return WrapResourceInHttpResponse.wrap(base.chooseContentType(), oo, HttpStatus.SC_OK)
        }
    }

    @Override
    HttpResponse run(SimProxyBase base, HttpResponse response) {
        throw new SimProxyTransformException('run(SimProxyBase base, HttpResponse response) not implemented.')
    }
}
