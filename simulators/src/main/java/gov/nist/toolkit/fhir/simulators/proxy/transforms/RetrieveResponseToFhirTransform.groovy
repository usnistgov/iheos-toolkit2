package gov.nist.toolkit.fhir.simulators.proxy.transforms

import ca.uhn.fhir.context.FhirContext
import gov.nist.toolkit.fhir.server.resourceMgr.FileSystemResourceCache
import gov.nist.toolkit.fhir.server.utility.WrapResourceInHttpResponse
import gov.nist.toolkit.fhir.simulators.proxy.util.BinaryPartSpec
import gov.nist.toolkit.fhir.simulators.proxy.util.ContentResponseTransform
import gov.nist.toolkit.fhir.simulators.proxy.util.MultipartParser2
import gov.nist.toolkit.fhir.simulators.proxy.util.RetrieveResponseParser
import gov.nist.toolkit.simcoresupport.proxy.exceptions.SimProxyTransformException
import gov.nist.toolkit.simcoresupport.proxy.util.SimProxyBase
import gov.nist.toolkit.testengine.fhir.FhirSupport
import gov.nist.toolkit.utilities.io.Io
import org.apache.commons.httpclient.HttpStatus
import org.apache.http.Header
import org.apache.http.HttpEntity
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
        FhirContext ctx = FileSystemResourceCache.ctx
        try {
            logger.info('Running RetrieveResponseToFhirTransform')
            String xmlBody
            Header contentTypeHeader = response.getHeaders('Content-Type')[0]
            if (!contentTypeHeader.value.startsWith('multipart'))
                throw new SimProxyTransformException('Not Implemented')
            HttpEntity entity = response.getEntity();
            byte[] partContent = Io.getBytesFromInputStream(entity.content)
            String partString = new String(partContent)
            List<BinaryPartSpec> parts = MultipartParser2.parse(partContent)
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

            String mimeType = contents[0].mimeType

            if (SimProxyBase.fhirTypes.contains(base.requestedContentType)) {
                Binary binary = new Binary()
                binary.contentTypeElement = new CodeType(mimeType)
                binary.setContent(contents[0].content)
                binary.id = contents[0].documentUniqueId

                return WrapResourceInHttpResponse.wrap(base.requestedContentType, binary, HttpStatus.SC_OK)
            } else {
                return WrapResourceInHttpResponse.wrap(mimeType, contents[0].content, HttpStatus.SC_OK, '')
            }

        } catch (Throwable e) {
            OperationOutcome oo = FhirSupport.operationOutcomeFromThrowable(e)
            return WrapResourceInHttpResponse.wrap(base.chooseContentType(), oo, HttpStatus.SC_OK)
        }
    }

    @Override
    HttpResponse run(SimProxyBase base, HttpResponse response) {
        throw new SimProxyTransformException('run(SimProxyBase base, HttpResponse response) not implemented.')
    }
}
