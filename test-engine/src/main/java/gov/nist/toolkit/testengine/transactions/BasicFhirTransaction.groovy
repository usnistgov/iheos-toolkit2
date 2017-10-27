package gov.nist.toolkit.testengine.transactions

import ca.uhn.fhir.context.FhirContext
import gov.nist.toolkit.testengine.engine.StepContext
import gov.nist.toolkit.xdsexception.client.MetadataException
import gov.nist.toolkit.xdsexception.client.XdsInternalException
import org.apache.axiom.om.OMElement
import org.hl7.fhir.instance.model.api.IBaseResource
/**
 *
 */
abstract class BasicFhirTransaction extends BasicTransaction {
    File resourceFile = null
    String urlExtension = ''
    String queryParams = ''
    FhirContext fhirCtx = FhirContext.forDstu3()

    abstract void doRun(IBaseResource resource, String urlExtension)

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
        IBaseResource resource = null
        if (resourceFile) {
            String content = resourceFile.text.trim()
            if (content.startsWith('<'))
                resource = fhirCtx.newXmlParser().parseResource(resourceFile.text)
            else
                resource = fhirCtx.newJsonParser().parseResource(resourceFile.text)
        }
        doRun(resource, urlExtension)
    }

    @Override
    protected void parseInstruction(OMElement part) throws XdsInternalException, MetadataException {
        String part_name = part.getLocalName();

        if (part_name == 'ResourceFile') {
            String localPath = part.getText()
            resourceFile = new File(localPath)
//            resourceFile = new File(this.testConfig.testplanDir, part.getText())

            if (!resourceFile.exists()) {
               // Try the testplanDir
                File tempFile = new File(this.testConfig.testplanDir, localPath)
                if (!tempFile.exists()) {
                    throw new XdsInternalException("resourceFile not found: " + resourceFile)
                } else {
                    resourceFile = tempFile
                }
            }

            testLog.add_name_value(this.instruction_output, "ResourceFile", resourceFile.path)
        } else if (part_name == 'UrlExtension') {
            urlExtension = part.getText()
        } else if (part_name == 'QueryParams') {
            queryParams = part.getText()
        } else
            parseBasicInstruction(part);
    }

    @Override
    protected String getRequestAction() {
        return null
    }

}
