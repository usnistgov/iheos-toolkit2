package gov.nist.toolkit.fhir.simulators.proxy.transforms

import gov.nist.toolkit.fhir.simulators.mhd.Query
import gov.nist.toolkit.fhir.simulators.mhd.SQTranslator
import gov.nist.toolkit.fhir.simulators.proxy.exceptions.SimProxyTransformException
import gov.nist.toolkit.fhir.simulators.proxy.util.ContentRequestTransform
import gov.nist.toolkit.fhir.simulators.proxy.util.ReturnableErrorException
import gov.nist.toolkit.fhir.simulators.proxy.util.SimProxyBase
import gov.nist.toolkit.utilities.io.Io
import org.apache.http.HttpRequest
import org.apache.http.RequestLine
import org.apache.http.entity.BasicHttpEntity
import org.apache.http.message.BasicHttpEntityEnclosingRequest
// the endpoint transform must be run before this one
class MhdToSQRequestTransform implements ContentRequestTransform {
    @Override
    HttpRequest run(SimProxyBase base, BasicHttpEntityEnclosingRequest request) {
        URI uri = new URI(base.uri)
        def query = uri.query
//        def parts = uri.split('\\?', 2)
//        if (parts.size() != 2) {
//            // throw error
//        }
//        String query = parts[1]
        RequestLine requestLine = request.requestLine
        BasicHttpEntityEnclosingRequest newRequest = new BasicHttpEntityEnclosingRequest(requestLine.method, requestLine.uri, requestLine.protocolVersion)

        String sq = new SQTranslator().run(query)
        String endpoint = base.targetEndpoint
        String service = uri.path
        String host = uri.host
        if (host == null)
            host = 'localhost'
        String port = Integer.toString(uri.port)
        if (port == '-1')
            port = 80
        String action = 'urn:ihe:iti:2007:RegistryStoredQuery'
        String body = Query.metadataInSoapWrapper(endpoint, action, sq)
        String headers = Query.header(service, host, port, action)
        BasicHttpEntity entity = new BasicHttpEntity()
        entity.setContent(Io.bytesToInputStream(body.bytes))
        headers.split('\n').each { String hdr ->
            if (hdr.contains(':')) {
                def (name, value) = hdr.split(':', 2)
                newRequest.addHeader(name, value)
            }
        }
        newRequest.setEntity(entity)
        return newRequest
    }


    @Override
    HttpRequest run(SimProxyBase base, HttpRequest request) throws ReturnableErrorException {
        throw new SimProxyTransformException("MhdToPnrContentTransform cannot handle requests of type ${request.getClass().getName() } ")
    }
}
