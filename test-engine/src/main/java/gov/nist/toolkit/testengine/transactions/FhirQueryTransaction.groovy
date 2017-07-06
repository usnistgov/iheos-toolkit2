package gov.nist.toolkit.testengine.transactions

import gov.nist.toolkit.testengine.engine.StepContext
import gov.nist.toolkit.testengine.fhir.FhirClient
import gov.nist.toolkit.xdsexception.client.MetadataException
import gov.nist.toolkit.xdsexception.client.XdsInternalException
import org.apache.axiom.om.OMElement
import org.apache.http.message.BasicStatusLine
import org.hl7.fhir.instance.model.api.IBaseResource

/**
 *
 */
class FhirQueryTransaction extends BasicFhirTransaction {
    FhirQueryTransaction(StepContext s_ctx, OMElement instruction, OMElement instruction_output) {
        super(s_ctx, instruction, instruction_output)
    }

    /**
     *
     * @param resource - always null
     * @param urlExtension
     */
    @Override
    void doRun(IBaseResource resource, String urlExtension) {
        def endpoint = useReportManager.get('Url')
        assert endpoint, 'FhirQueryTransaction - UseReport Url is null'
        assert queryParams, 'FhirQueryTransaction - UseReport QueryParams is null'

        def fullEndpoint = "${endpoint}${queryParams}"

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
