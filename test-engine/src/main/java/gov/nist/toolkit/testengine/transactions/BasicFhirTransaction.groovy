package gov.nist.toolkit.testengine.transactions

import ca.uhn.fhir.context.FhirContext
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
abstract class BasicFhirTransaction extends BasicTransaction {
    File resourceFile = null
    String urlExtension = ''
    FhirContext fhirCtx = FhirContext.forDstu3()

    abstract void doRun(IBaseResource resource, String urlExtension)

    /**
     * Transaction class can override this method in its doRun method
     * @param resource
     * @param urlExtension added to FHIR Service Base URL - must start with '/'
     */
    void doPost(IBaseResource resource, String theUrlExtension) {
        assert endpoint, 'TestClient:BasicFhirTransaction: endpoint is null'
        def (BasicStatusLine statusLine, String content, FhirId fhirId) = FhirClient.post(new URI("${endpoint}${theUrlExtension}"), fhirCtx.newJsonParser().encodeResourceToString(resource))
        if (statusLine.statusCode in 400..599)  {
            stepContext.set_error("Status:${statusLine.toString()}")
        }
    }

    protected BasicFhirTransaction(StepContext s_ctx, OMElement instruction, OMElement instruction_output) {
        super(s_ctx, instruction, instruction_output)
    }

    /**
     *
     * @param request not used for FHIR transactions
     * @throws Exception
     */
    @Override
    protected void run(OMElement request) throws Exception {
        useMtom = false
        if (resourceFile) {
            IBaseResource resource = fhirCtx.newJsonParser().parseResource(resourceFile.text)
            doRun(resource, urlExtension)
        }
    }

    @Override
    protected void parseInstruction(OMElement part) throws XdsInternalException, MetadataException {
        String part_name = part.getLocalName();

        if (part_name == 'ResourceFile') {
            resourceFile = new File(this.testConfig.testplanDir, part.getText())
            testLog.add_name_value(this.instruction_output, "ResourceFile", resourceFile.path)
        } else if (part_name == 'UrlExtension') {
            urlExtension = part.getText()
        } else
            parseBasicInstruction(part);
    }

    @Override
    protected String getRequestAction() {
        return null
    }

}
