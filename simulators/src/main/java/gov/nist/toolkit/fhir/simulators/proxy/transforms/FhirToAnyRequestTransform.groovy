package gov.nist.toolkit.fhir.simulators.proxy.transforms

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.fhir.simulators.mhd.SQParamTranslator
import gov.nist.toolkit.fhir.simulators.proxy.exceptions.SimProxyTransformException
import gov.nist.toolkit.fhir.simulators.proxy.util.ContentRequestTransform
import gov.nist.toolkit.fhir.simulators.proxy.util.ReturnableErrorException
import gov.nist.toolkit.fhir.simulators.proxy.util.SimProxyBase
import org.apache.http.HttpRequest
import org.apache.http.message.BasicHttpEntityEnclosingRequest
import org.apache.log4j.Logger

class FhirToAnyRequestTransform implements ContentRequestTransform {
    static private final Logger logger = Logger.getLogger(FhirToAnyRequestTransform)

    @Override
    HttpRequest run(SimProxyBase base, BasicHttpEntityEnclosingRequest request) {
        run(base, (HttpRequest) request)
        //throw new SimProxyTransformException("FhirToAnyRequestTransform cannot handle requests of type ${request.getClass().getName() } ")
    }

    @Override
    HttpRequest run(SimProxyBase base, HttpRequest request) throws ReturnableErrorException {
        logger.info('Running FhirToAnyRequestTransform')

        if (base.endpoint.transactionTypeName == 'DocumentReference') {
            String id = base.endpoint.id
            base.setTargetType(ActorType.REGISTRY, TransactionType.STORED_QUERY)

            def sqModel = [:]   // model is [queryParamName: [values]]
            sqModel[SQParamTranslator.queryType] = [SQParamTranslator.GetDocs]
            sqModel[SQParamTranslator.entryUUID] = ["urn:uuid:${id}"]
            return MhdToSQRequestTransform.sqAsHttpRequest(base, request, sqModel)
        } else if (base.endpoint.transactionTypeName == 'Binary') {

        }
        throw new SimProxyTransformException("Query for ${base.endpoint?.transactionType?.name} not supported yet")
    }

//    String getRequestedContentType(HttpRequest request, String name) {
//        List<Header> headers = request.getHeaders(name)
//        headers[0].value
//    }

}
