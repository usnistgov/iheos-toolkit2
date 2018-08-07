package gov.nist.toolkit.fhir.simulators.proxy.transforms

import gov.nist.toolkit.actortransaction.shared.ActorType
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.fhir.simulators.proxy.util.ContentRequestTransform
import gov.nist.toolkit.simcoresupport.proxy.util.ReturnableErrorException
import gov.nist.toolkit.simcoresupport.proxy.util.SimProxyBase
import org.apache.http.HttpRequest
import org.apache.http.message.BasicHttpEntityEnclosingRequest
import org.apache.log4j.Logger

class FhirReadBinaryTransform implements ContentRequestTransform {
    static private final Logger logger = Logger.getLogger(FhirReadBinaryTransform)

    @Override
    HttpRequest run(SimProxyBase base, BasicHttpEntityEnclosingRequest request) {
        run(base, (HttpRequest) request)
    }

    @Override
    HttpRequest run(SimProxyBase base, HttpRequest request) throws ReturnableErrorException {
        logger.info('Running FhirReadBinaryTransform')
        base.setTargetType(ActorType.REPOSITORY, TransactionType.RETRIEVE)




        return null
    }
}
