package gov.nist.toolkit.fhir.simulators.proxy.transforms

import gov.nist.toolkit.fhir.simulators.proxy.exceptions.SimProxyTransformException
import gov.nist.toolkit.fhir.simulators.proxy.util.ContentRequestTransform
import gov.nist.toolkit.fhir.simulators.proxy.util.ReturnableErrorException
import gov.nist.toolkit.fhir.simulators.proxy.util.SimProxyBase
import org.apache.http.HttpRequest
import org.apache.http.message.BasicHttpEntityEnclosingRequest

class MhdToSQRequestTransform implements ContentRequestTransform {
    @Override
    HttpRequest run(SimProxyBase base, BasicHttpEntityEnclosingRequest request) {
        String uri = request.requestLine.uri
        return null
    }


    @Override
    HttpRequest run(SimProxyBase base, HttpRequest request) throws ReturnableErrorException {
        throw new SimProxyTransformException("MhdToPnrContentTransform cannot handle requests of type ${request.getClass().getName() } ")
    }
}
