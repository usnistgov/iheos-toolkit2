package gov.nist.toolkit.testengine.transactions

import ca.uhn.fhir.context.FhirContext
import gov.nist.toolkit.fhir.context.ToolkitFhirContext
import gov.nist.toolkit.fhir.utility.FhirClient
import gov.nist.toolkit.fhir.utility.FhirId
import gov.nist.toolkit.testengine.engine.StepContext
import gov.nist.toolkit.testengine.fhir.FhirSupport
import gov.nist.toolkit.utilities.xml.Util
import gov.nist.toolkit.xdsexception.client.MetadataException
import gov.nist.toolkit.xdsexception.client.XdsInternalException
import org.apache.axiom.om.OMElement
import org.apache.http.message.BasicStatusLine
import org.hl7.fhir.dstu3.model.OperationOutcome
import org.hl7.fhir.instance.model.api.IBaseResource
/**
 *
 */
class FhirReadTransaction extends BasicFhirTransaction {
    boolean requestXml = false
    boolean mustReturn = false
    FhirContext ctx = ToolkitFhirContext.get()

    FhirReadTransaction(StepContext s_ctx, OMElement instruction, OMElement instruction_output) {
        super(s_ctx, instruction, instruction_output)

        defaultEndpointProcessing = false
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

        def contentType = (requestXml) ? 'application/fhir+xml' : 'application/fhir+json'
        testLog.add_name_value(instruction_output, 'OutHeader', "GET ${fullEndpoint}")
        def (BasicStatusLine statusLine, String content) = FhirClient.get(new URI(fullEndpoint), contentType)
        if (statusLine.statusCode in 400..599)  {
            stepContext.set_error("Status:${statusLine}")
        } else {
            if (content) {
                // content is either JSON or XML
                if (requestXml) {
                    OMElement f = testLog.add_simple_element(instruction_output, "Format")
                    f.addAttribute('value', 'xml', null)
                    testLog.add_name_value(instruction_output, "Result", FhirSupport.format(content));
                } else {
                    // by default JSON is requested
                    OMElement f = testLog.add_simple_element(instruction_output, "Format")
                    f.addAttribute('value', 'json', null)
                    IBaseResource baseResource = FhirSupport.parse(content)
                    String xml = ctx.newXmlParser().encodeResourceToString(baseResource)
                    OMElement xmlo = Util.parse_xml(xml)
//                xml = new OMFormatter(xml).toString()
                    testLog.add_name_value(instruction_output, "Result", xmlo);
                }
                if (mustReturn) {
                    def expectedResourceType = resourceTypeFromUrl(fullEndpoint.toString())
                    IBaseResource returnedResource = FhirSupport.parse(content)
                    def theResourceType = returnedResource.class.simpleName
                    if (expectedResourceType != theResourceType) {
                        stepContext.set_error("Expected resource of type ${expectedResourceType} but got ${theResourceType} instead")
                        if (returnedResource instanceof OperationOutcome) {
                            OperationOutcome oo = returnedResource
                            oo.issue.each {
                                stepContext.set_error(it.diagnostics)
                            }
                        }
                    }
                    FhirId requestId = new FhirId(fullEndpoint)
                    FhirId responseId = new FhirId(returnedResource.id)
                    if (requestId.withoutHistory() != responseId.withoutHistory())
                        stepContext.set_error("Requested ID ${requestId.withoutHistory()} but received ${responseId.withoutHistory()}")
                }
            } else {
                testLog.add_name_value(instruction_output, "Result", 'None');
                if (mustReturn)
                    stepContext.set_error("A Resource was not returned")
            }
        }

    }

    static resourceTypeFromUrl(url) {
        def fhirId = new FhirId(url)
        return fhirId.type
    }

    @Override
    protected String getBasicTransactionName() {
        return 'fhir'
    }

    @Override
    protected void parseInstruction(OMElement part) throws XdsInternalException, MetadataException {
        String part_name = part.getLocalName()

        if (part_name == 'RequestXml') {
            requestXml = true;
        }
        else if (part_name == 'MustReturn') {
            mustReturn = true;
        }
        else {
            super.parseInstruction(part)
        }
    }

}
