package gov.nist.toolkit.fhir.simulators.proxy.transforms

import ca.uhn.fhir.context.FhirContext
import gov.nist.toolkit.fhir.resourceMgr.ResourceCache
import gov.nist.toolkit.fhir.simulators.fhir.WrapResourceInHttpResponse
import gov.nist.toolkit.fhir.simulators.proxy.exceptions.SimProxyTransformException
import gov.nist.toolkit.fhir.simulators.proxy.util.ContentResponseTransform
import gov.nist.toolkit.fhir.simulators.proxy.util.RetrieveResponseParser
import gov.nist.toolkit.fhir.simulators.proxy.util.SimProxyBase
import gov.nist.toolkit.utilities.io.Io
import gov.nist.toolkit.xdsexception.ExceptionUtil
import org.apache.commons.httpclient.HttpStatus
import org.apache.http.Header
import org.apache.http.HttpResponse
import org.apache.http.message.BasicHttpResponse
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
            Binary binary = new Binary()
            binary.contentTypeElement = new CodeType(contents[0].mimeType)
            binary.setContent(contents[0].content)
            binary.id = contents[0].documentUniqueId

            return WrapResourceInHttpResponse.wrap(base, binary, HttpStatus.SC_OK)


        } catch (Throwable e) {
            OperationOutcome oo = new OperationOutcome()
            OperationOutcome.OperationOutcomeIssueComponent com = new OperationOutcome.OperationOutcomeIssueComponent()
            com.setSeverity(OperationOutcome.IssueSeverity.FATAL)
            com.setCode(OperationOutcome.IssueType.EXCEPTION)
            com.setDiagnostics(ExceptionUtil.exception_details(e))
            oo.addIssue(com)
            return WrapResourceInHttpResponse.wrap(base, oo, HttpStatus.SC_OK)
        }
    }

    @Override
    HttpResponse run(SimProxyBase base, HttpResponse response) {
        throw new SimProxyTransformException('run(SimProxyBase base, HttpResponse response) not implemented.')
    }
}
