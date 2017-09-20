package gov.nist.toolkit.simulators.proxy.transforms

import ca.uhn.fhir.context.FhirContext
import gov.nist.toolkit.installation.ResourceCache
import gov.nist.toolkit.simulators.fhir.OperationOutcomeGenerator
import gov.nist.toolkit.simulators.proxy.exceptions.SimProxyTransformException
import gov.nist.toolkit.simulators.proxy.util.ContentResponseTransform
import gov.nist.toolkit.simulators.proxy.util.ResponsePartParser
import gov.nist.toolkit.simulators.proxy.util.SimProxyBase
import gov.nist.toolkit.soap.http.SoapFault
import gov.nist.toolkit.utilities.io.Io
import org.apache.http.Header
import org.apache.http.HttpResponse
import org.apache.http.HttpStatus
import org.apache.http.ProtocolVersion
import org.apache.http.entity.StringEntity
import org.apache.http.message.BasicHttpResponse
import org.apache.http.message.BasicStatusLine
import org.apache.http.protocol.HttpDateGenerator
import org.apache.log4j.Logger
import org.hl7.fhir.dstu3.model.OperationOutcome
/**
 *
 */
class RegistryResponseToOperationOutcomeTransform implements ContentResponseTransform {
    static private final Logger logger = Logger.getLogger(RegistryResponseToOperationOutcomeTransform.class);

    @Override
    HttpResponse run(SimProxyBase base, HttpResponse response) {
        throw new SimProxyTransformException('run(SimProxyBase base, HttpResponse response) not implemented.')
    }

    @Override
    HttpResponse run(SimProxyBase base, BasicHttpResponse response) {
        FhirContext ctx = ResourceCache.ctx

        logger.info('Running RegistryResponseToOperationOutcomeTransform')
        String xmlBody
        Header contentTypeHeader = response.getHeaders('Content-Type')[0]
        if (contentTypeHeader.value.startsWith('multipart')) {
            String partContent = Io.getStringFromInputStream(response.getEntity().content)
            BasicHttpResponse part = ResponsePartParser.parse(partContent)
            xmlBody = Io.getStringFromInputStream(part.getEntity().content)
        } else
            throw new SimProxyTransformException('Not Implemented')
        def root = new XmlSlurper().parseText(xmlBody)
        if (root.name() == 'Envelope') {
            def fault = root?.Body?.Fault
            if (fault) {
                String code = fault.Code?.Value?.text()
                println "Fault code is ${code}"
                String reason = fault.Reason?.Text?.text()
                println "Reason is ${reason}"
                SoapFault soapFault = new SoapFault(code, reason)
                OperationOutcome operationOutcome = OperationOutcomeGenerator.translate(soapFault)
                BasicHttpResponse outcome = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion('HTTP', 1,1), HttpStatus.SC_OK, 'OK'))
                outcome.addHeader('Content-Type', base.clientContentType)
                outcome.addHeader('Date', new HttpDateGenerator().currentDate)
                String content
                if (base.clientContentType.contains('json')) {
                    content = ctx.newJsonParser().encodeResourceToString(operationOutcome)
                } else {
                    content = ctx.newXmlParser().encodeResourceToString(operationOutcome)
                }
                outcome.setEntity(new StringEntity(content))
                return outcome
            }
        }
        throw new SimProxyTransformException('Not Implemented')
    }
}
