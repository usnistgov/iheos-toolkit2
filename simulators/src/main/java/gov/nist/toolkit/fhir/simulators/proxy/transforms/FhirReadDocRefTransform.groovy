package gov.nist.toolkit.fhir.simulators.proxy.transforms

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.fhir.simulators.mhd.SQParamTranslator
import gov.nist.toolkit.fhir.simulators.proxy.util.ContentRequestTransform
import gov.nist.toolkit.simcoresupport.proxy.util.ReturnableErrorException
import gov.nist.toolkit.simcoresupport.proxy.util.SimProxyBase
import org.apache.http.HttpRequest
import org.apache.http.message.BasicHttpEntityEnclosingRequest
import org.apache.log4j.Logger

class FhirReadDocRefTransform implements ContentRequestTransform {
    static private final Logger logger = Logger.getLogger(FhirReadDocRefTransform)

    @Override
    HttpRequest run(SimProxyBase base, BasicHttpEntityEnclosingRequest request) {
        run(base, (HttpRequest) request)
    }

    @Override
    HttpRequest run(SimProxyBase base, HttpRequest request) throws ReturnableErrorException {
        logger.info('Running FhirReadDocRefTransform')
        String id = base.endpoint.id
        base.setTargetType(ActorType.REGISTRY, TransactionType.STORED_QUERY)

        def sqModel = [:]   // model is [queryParamName: [values]]
        sqModel[SQParamTranslator.queryType] = [SQParamTranslator.GetDocs]
        sqModel[SQParamTranslator.entryUUID] = ["urn:uuid:${id}"]
        return MhdToSQRequestTransform.sqAsHttpRequest(base, request, sqModel)
    }
}
