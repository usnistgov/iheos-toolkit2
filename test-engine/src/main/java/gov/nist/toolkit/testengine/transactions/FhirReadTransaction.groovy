package gov.nist.toolkit.testengine.transactions

import gov.nist.toolkit.testengine.engine.StepContext
import gov.nist.toolkit.fhir.utility.FhirClient
import gov.nist.toolkit.xdsexception.client.MetadataException
import gov.nist.toolkit.xdsexception.client.XdsInternalException
import org.apache.axiom.om.OMElement
import org.apache.http.message.BasicStatusLine
import org.hl7.fhir.instance.model.api.IBaseResource

/**
 *
 */
class FhirReadTransaction extends BasicFhirTransaction {
    FhirReadTransaction(StepContext s_ctx, OMElement instruction, OMElement instruction_output) {
        super(s_ctx, instruction, instruction_output)
    }

    /**
     *
     * @param resource - always null
     * @param urlExtension
     */
    @Override
    void doRun(IBaseResource resource, String urlExtension) {

        def fullEndpoint = useReportManager.get('Ref')
        assert fullEndpoint, 'FhirReadTransaction: Ref is null'

        reportManager.add('Url', fullEndpoint)

        def (BasicStatusLine statusLine, String content) = FhirClient.get(new URI(fullEndpoint))
        if (statusLine.statusCode in 400..599)  {
            stepContext.set_error("Status:${statusLine}")
        }
        testLog.add_name_value(instruction_output, "Result", content);

    }

    @Override
    protected String getBasicTransactionName() {
        return 'fhir'
    }

    @Override
    protected void parseInstruction(OMElement part) throws XdsInternalException, MetadataException {
        super.parseInstruction(part)
    }

}
