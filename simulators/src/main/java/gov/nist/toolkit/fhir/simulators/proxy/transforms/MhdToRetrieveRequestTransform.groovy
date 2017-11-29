package gov.nist.toolkit.fhir.simulators.proxy.transforms

import gov.nist.toolkit.fhir.simulators.proxy.util.ContentRequestTransform
import gov.nist.toolkit.fhir.simulators.proxy.util.ReturnableErrorException
import gov.nist.toolkit.fhir.simulators.proxy.util.SimProxyBase
import org.apache.http.HttpRequest
import org.apache.http.message.BasicHttpEntityEnclosingRequest
import org.apache.log4j.Logger

class MhdToRetrieveRequestTransform implements ContentRequestTransform {
        static private final Logger logger = Logger.getLogger(MhdToRetrieveRequestTransform)

    @Override
    HttpRequest run(SimProxyBase base, BasicHttpEntityEnclosingRequest request) {
        return null
    }

    @Override
    HttpRequest run(SimProxyBase base, HttpRequest request) throws ReturnableErrorException {
        return null
    }
}
