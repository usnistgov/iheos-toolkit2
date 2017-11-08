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
class FhirSearchTransaction extends BasicFhirTransaction {
    FhirSearchTransaction(StepContext s_ctx, OMElement instruction, OMElement instruction_output) {
        super(s_ctx, instruction, instruction_output)
    }

    /**
     *
     * @param resource - always null
     * @param urlExtension
     */
    @Override
    void doRun(IBaseResource resource, String urlExtension) {
        def base = useReportManager.get('Base')
        assert base, 'FhirSearchTransaction - UseReport Base_Type is null'
        assert queryParams, 'FhirSearchTransaction - UseReport QueryParams is null'

        def fullEndpoint = "${base}${queryParams}"

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
