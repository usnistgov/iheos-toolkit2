package gov.nist.toolkit.testengine.transactions

import gov.nist.toolkit.testengine.engine.StepContext
import gov.nist.toolkit.testengine.fhir.FhirClient
import gov.nist.toolkit.testengine.fhir.FhirId
import gov.nist.toolkit.xdsexception.client.MetadataException
import gov.nist.toolkit.xdsexception.client.XdsInternalException
import org.apache.axiom.om.OMElement
import org.apache.http.message.BasicStatusLine
import org.hl7.fhir.instance.model.api.IBaseResource

/**
 *
 */
class FhirCreateTransaction extends BasicFhirTransaction {
    FhirCreateTransaction(StepContext s_ctx, OMElement instruction, OMElement instruction_output) {
        super(s_ctx, instruction, instruction_output)
    }

    @Override
    void doRun(IBaseResource resource, String urlExtension) {
        assert endpoint, 'TestClient:FhirCreateTransaction: endpoint is null'

        if (urlExtension && !urlExtension.startsWith('/'))
            urlExtension = "/${urlExtension}"

        // update endpoint to include urlExtension
        // the base variable has to be updated to it is logged to log.xml

        def fullEndpoint = "${endpoint}${urlExtension}"

        reportManager.add('Url', fullEndpoint)

        def (BasicStatusLine statusLine, String content, FhirId fhirId) = FhirClient.post(new URI(fullEndpoint), fhirCtx.newJsonParser().encodeResourceToString(resource))
        if (statusLine.statusCode in 400..599)  {
            stepContext.set_error("Status:${statusLine}")
        }
//        reportManager.add("FhirIdWithHistory", fhirId.toString())
//        reportManager.add('RefWithHistory', "${endpoint}/${fhirId}")
        reportManager.add("FhirId", fhirId.withoutHistory())
        reportManager.add('Ref', "${endpoint}/${fhirId.withoutHistory()}")
    }

    @Override
    protected void parseInstruction(OMElement part) throws XdsInternalException, MetadataException {
        super.parseInstruction(part)
    }

    @Override
    protected String getBasicTransactionName() {
        return 'fhir'
    }

}
