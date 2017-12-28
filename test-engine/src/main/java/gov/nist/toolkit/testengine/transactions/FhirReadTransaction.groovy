package gov.nist.toolkit.testengine.transactions

import ca.uhn.fhir.context.FhirContext
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.fhir.context.ToolkitFhirContext
import gov.nist.toolkit.fhir.server.utility.FhirClient
import gov.nist.toolkit.fhir.server.utility.FhirId
import gov.nist.toolkit.testengine.engine.StepContext
import gov.nist.toolkit.testengine.fhir.FhirSupport
import gov.nist.toolkit.utilities.xml.Util
import gov.nist.toolkit.xdsexception.client.MetadataException
import gov.nist.toolkit.xdsexception.client.XdsInternalException
import org.apache.axiom.om.OMElement
import org.apache.http.message.BasicStatusLine
import org.hl7.fhir.dstu3.model.Binary
import org.hl7.fhir.dstu3.model.DocumentReference
import org.hl7.fhir.dstu3.model.OperationOutcome
import org.hl7.fhir.dstu3.model.Resource
import org.hl7.fhir.instance.model.api.IBaseResource
/**
 *
 */
class FhirReadTransaction extends BasicFhirTransaction {
    // if both of these are specified, requestType takes precesencse
    boolean requestXml = false
    String requestType = null
    String referenceDocument = null  // relative path
    String requestAcceptType = null

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

        def fullEndpoint = useReportManager?.get('Ref')
        if (!fullEndpoint) {
            if (urlExtension) {
                if (urlExtension && !urlExtension.startsWith('/'))
                    urlExtension = "/${urlExtension}"
                def fhirBase = testConfig.site.getEndpoint(TransactionType.FHIR, false, false)
                assert fhirBase, "FHIRBase is null"
                fullEndpoint = "${fhirBase}${urlExtension}"
            }
        }
        assert fullEndpoint, 'FhirReadTransaction: Ref and UrlExtension are null'
        endpoint = fullEndpoint  // so it shows up on the UI
        reportManager.add('Url', fullEndpoint)

        def acceptType = (requestXml) ? 'application/fhir+xml' : 'application/fhir+json'
        if (requestType)
            acceptType = requestType
        if (requestAcceptType)
            acceptType = requestAcceptType
        testLog.add_name_value(instruction_output, 'OutHeader', "GET ${fullEndpoint}")

        BasicStatusLine statusLine
        String content
        byte[] contentBytes
        String returnedContentType
        if (requestAcceptType) {
            (statusLine, returnedContentType, contentBytes) = FhirClient.getBytes(new URI(fullEndpoint), acceptType)
            content = new String(contentBytes)
            if (referenceDocument) {
                File referenceFile = new File(testConfig.testplanDir, referenceDocument)
                byte[] referenceBytes = referenceFile.bytes

                if (returnedContentType.contains('fhir')) {
                    // returned Binary instead of just the byte stream
                    IBaseResource returned = FhirSupport.parse(content)
                    assert returned instanceof Binary, "Since return is type ${returnedContentType}, content must be Binary resource - got ${returned.class.simpleName} instead"
                    Binary returnedBinary = returned
                    byte[] returnedBytes = returnedBinary.content
                    if (referenceBytes !=  returnedBytes) {
                        stepContext.set_error("Returned bytes do not match those sent")
                        stepContext.set_error("Sent ${referenceBytes.size()}")
                        stepContext.set_error("Received ${contentBytes.size()}")
                        stepContext.set_error(describe(referenceBytes, contentBytes))
                        return
                    }
                } else {
                    // natural content returned (not a resource)
                    if (referenceBytes != contentBytes) {
                        stepContext.set_error("Returned bytes do not match those sent")
                        stepContext.set_error("Sent ${referenceBytes.size()}")
                        stepContext.set_error("Received ${contentBytes.size()}")
                        stepContext.set_error(describe(referenceBytes, contentBytes))
                        return
                    }
                    return
                }
            }
        } else {
            (statusLine, content) = FhirClient.get(new URI(fullEndpoint), acceptType)
        }

        IBaseResource baseResource = null
        Resource returnedResource = null
        if (statusLine.statusCode in 400..599)  {
            stepContext.set_error("Status:${statusLine}")
        } else {
            if (content) {
                baseResource = FhirSupport.parse(content)
                if (baseResource instanceof Resource)
                    returnedResource = baseResource
                // content is either JSON or XML
                if (requestXml) {
                    OMElement f = testLog.add_simple_element(instruction_output, "Format")
                    f.addAttribute('value', 'xml', null)
                    testLog.add_name_value(instruction_output, "Result", FhirSupport.format(content));
                } else {
                    // by default JSON is requested
                    OMElement f = testLog.add_simple_element(instruction_output, "Format")
                    f.addAttribute('value', 'json', null)
                    String xml = ctx.newXmlParser().encodeResourceToString(returnedResource)
                    OMElement xmlo = Util.parse_xml(xml)
//                xml = new OMFormatter(xml).toString()
                    testLog.add_name_value(instruction_output, "Result", xmlo);
                }
                if (returnedResource instanceof OperationOutcome) {
                    OperationOutcome oo = returnedResource
                    oo.issue.each {
                        stepContext.set_error(it.diagnostics)
                    }
                }
                if (mustReturn) {
                    def expectedResourceType = resourceTypeFromUrl(fullEndpoint.toString())
                    def theResourceType = returnedResource.class.simpleName
                    if (expectedResourceType != theResourceType) {
                        stepContext.set_error("Expected resource of type ${expectedResourceType} but got ${theResourceType} instead")
                        if (returnedResource instanceof OperationOutcome) {
                            def issues = FhirSupport.operationOutcomeIssues(returnedResource)
                            stepContext.set_error(issues)
                        }
                    }
                    FhirId requestId = new FhirId(fullEndpoint)
                    FhirId returnedId = new FhirId(returnedResource)
                    if (requestId.id != returnedId.id)
                        stepContext.set_error("Requested ID ${requestId.id} but received ${returnedId.id}")
                }
                if (returnedResource instanceof DocumentReference) {
                    String binaryReference = returnedResource?.content?.attachment?.get(0)?.url
                    reportManager.add('BinaryUrl', binaryReference)
                }
            } else {
                testLog.add_name_value(instruction_output, "Result", 'None');
                if (mustReturn)
                    stepContext.set_error("A Resource was not returned")
            }
            if (referenceDocument && returnedResource) {
                if (!(returnedResource instanceof Binary)) {
                    stepContext.set_error("referenceDocument provided but returned resource of type ${returnedResource.class.simpleName} instead of Binary")
                } else {
                    Binary returnedBinary = returnedResource
                    File referenceFile = new File(testConfig.testplanDir, referenceDocument)
                    Binary referenceBinary = FhirSupport.binaryFromFile(referenceFile)
                    byte[] referenceBytes = referenceBinary.content
                    byte[] returnedBytes = returnedBinary.content

                    String referenceString = new String(referenceBytes)
                    String returnedString = new String(referenceBytes)

                    assert referenceString == returnedString

                    if (referenceBytes != returnedBytes) {
                        stepContext.set_error("Returned bytes do not match those sent")
                        stepContext.set_error("Sent ${referenceBytes.size()}")
                        stepContext.set_error("Received ${returnedBytes.size()}")
                        stepContext.set_error(describe(referenceBytes, returnedBytes))
                        if (referenceDocument.endsWith('.txt')) {
                            stepContext.set_error("Sent ${new String(referenceBytes).replaceAll('\n','\\n')}")
                            stepContext.set_error("Back ${new String(returnedBytes).replaceAll('\n','\\n')} ")
                        }
                    }
                }
            }
        }

    }

    String describe(byte[] a, byte[] b) {
        if (a == b) return ''
        if (a.size() == b.size()) return 'corrupted'
        StringBuilder buf = new StringBuilder()

        buf.append('a starts with ').append(a[0]).append(a[1]).append(a[2]).append('\n')
        buf.append('b starts with ').append(b[0]).append(b[1]).append(b[2]).append('\n')
        buf.append('a ends with ').append(a[a.size()-1]).append(a[a.size()-2]).append(a[a.size()-3]).append('\n')
        buf.append('b ends with ').append(b[b.size()-1]).append(b[b.size()-2]).append(b[b.size()-3]).append('\n')

        return buf.toString()
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
        else if (part_name == 'RequestType') {
            requestType = part.text;
        }
        else if (part_name == 'ReferenceDocument') {
            referenceDocument = part.text;
        }
        else if (part_name == 'AcceptType') {
            requestAcceptType = part.text;
        }
        else {
            super.parseInstruction(part)
        }
    }

}
