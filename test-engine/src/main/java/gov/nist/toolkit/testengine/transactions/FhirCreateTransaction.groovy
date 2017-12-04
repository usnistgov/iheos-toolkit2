package gov.nist.toolkit.testengine.transactions

import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.fhir.utility.FhirClient
import gov.nist.toolkit.fhir.utility.FhirId
import gov.nist.toolkit.testengine.engine.StepContext
import gov.nist.toolkit.testengine.engine.UniqueIdAllocator
import gov.nist.toolkit.testengine.fhir.FhirSupport
import gov.nist.toolkit.utilities.io.Io
import gov.nist.toolkit.xdsexception.client.MetadataException
import gov.nist.toolkit.xdsexception.client.XdsInternalException
import org.apache.axiom.om.OMElement
import org.apache.http.message.BasicStatusLine
import org.hl7.fhir.dstu3.model.*
import org.hl7.fhir.instance.model.api.IBaseResource
/**
 *
 */
class FhirCreateTransaction extends BasicFhirTransaction {
    FhirCreateTransaction(StepContext s_ctx, OMElement instruction, OMElement instruction_output) {
        super(s_ctx, instruction, instruction_output)
    }

    def updateMasterIdentifier(def resource) {
        if ((resource instanceof DocumentManifest) || (resource instanceof DocumentReference)) {
            Identifier id = resource.getMasterIdentifier()
            id.value = UniqueIdAllocator.getInstance(null).allocate()
            resource.masterIdentifier = id
        } else if (resource instanceof Bundle) {
            Bundle bundle = resource
            bundle.entry.each { Bundle.BundleEntryComponent comp ->
                Resource res = comp.getResource()
                updateMasterIdentifier(res)
            }
        }
    }

    def updatePidIdentifier(def resource, String value, String system) {
        if ((resource instanceof DocumentManifest) || (resource instanceof DocumentReference)) {
            List<Identifier> ids = resource.getIdentifier()
            if (ids.size() > 0) {
                Identifier id = ids[0]
                id.system = system
                id.value = value
            }
        } else if (resource instanceof Bundle) {
            Bundle bundle = resource
            bundle.entry.each { Bundle.BundleEntryComponent comp ->
                Resource res = comp.getResource()
                updatePidIdentifier(res, value, system)
            }
        }
    }

    def updatePatientReference(def resource, String patientReference) {
        if ((resource instanceof DocumentManifest)) {
            resource.subject.reference = patientReference
        }
        else if ((resource instanceof DocumentReference)) {
            resource.subject.reference = patientReference
        } else if (resource instanceof Bundle) {
            Bundle bundle = resource
            bundle.entry.each { Bundle.BundleEntryComponent comp ->
                Resource res = comp.getResource()
                updatePatientReference(res, patientReference)
            }
        }
    }

    String getBaseUrl() {
        testConfig.site.getEndpoint(TransactionType.FHIR, false, false)
    }

    @Override
    void doRun(IBaseResource resource, String urlExtension) {
        assert endpoint, 'TestClient:FhirCreateTransaction: endpoint is null'

        String pid_value = null
        String pid_system = null
        String patientReference = null

        if (useReportManager) {
            pid_value = useReportManager.get('$pid_value$');
            pid_system = useReportManager.get('$pid_system$');
            patientReference = useReportManager.get('$patient_reference$')
        }

        includeLocalReferences(resource)

        if (patientReference)
            updatePatientReference(resource, patientReference)

        if (pid_value && pid_system)
            updatePidIdentifier(resource, pid_value, pid_system)

        // assign new new masterIdentifier to all DocumentRefernce and Documeent Manifest objects
        if (resource instanceof Resource)
            updateMasterIdentifier(resource)

        if (urlExtension && !urlExtension.startsWith('/'))
            urlExtension = "/${urlExtension}"

        // update endpoint to include urlExtension
        // the base variable has to be updated to it is logged to log.xml

        def fullEndpoint = "${endpoint}${urlExtension}"

        reportManager.add('Base', getBaseUrl())   // endpoint)
        reportManager.add('Url', fullEndpoint)

//        fullEndpoint = fullEndpoint.replace('7777', '6666')

        testLog.add_name_value(instruction_output, 'OutHeader', "POST ${fullEndpoint}")

        def sendContent = fhirCtx.newJsonParser().setPrettyPrint(true).encodeResourceToString(resource)
        testLog.add_name_value(instruction_output, 'InputMetadata', sendContent)

        def dmCount = 0
        def drCount = 0
        def otherCount = 0

        // No fhirID from transaction
        def (BasicStatusLine statusLine, String content, FhirId fhirId) = FhirClient.post(new URI(fullEndpoint), sendContent)
        if (content) {
            IBaseResource baseResource = FhirSupport.parse(content)
            if (baseResource instanceof OperationOutcome) {
                OperationOutcome oo = (OperationOutcome) baseResource
                testLog.add_name_value(instruction_output, "Result", fhirCtx.newXmlParser().setPrettyPrint(true).encodeResourceToString(oo));
                simpleErrorMsg(oo, stepContext)
            } else if (baseResource instanceof Bundle) {
                testLog.add_name_value(instruction_output, "Result", fhirCtx.newXmlParser().setPrettyPrint(true).encodeResourceToString(baseResource));
                Bundle bundle = baseResource
                bundle.entry.each { Bundle.BundleEntryComponent comp ->
                    if (comp?.response?.status != '200')
                        stepContext.set_error("Response Bundle reported status of ${comp.response.status} for component ${comp.fullUrl} (${comp.id}")
//                    assert comp.response.status == '200'
                    if (comp.fullUrl) {
                        def reportName
                        IBaseResource resource1 = comp.getResource()
                        if (resource1 instanceof DocumentManifest) {
                            reportName = "DM${dmCount++}"
                        } else if (resource1 instanceof DocumentReference) {
                            reportName = "DR${drCount++}"
                        } else {
                            reportName = "OTHER${otherCount++}"
                        }
                        def url = new FhirId(comp.fullUrl).withoutHistory()
                        reportManager.add(reportName, url)
                        reportManager.add("REF_${reportName}", "${getBaseUrl()}/${url}")
                    }
//                    FhirId myId = new FhirId(comp.response?.outcome?.id)
//                    if (myId)
//                        reportManager.add('Ref', myId.withoutHistory())
                }
            } else {
                stepContext.set_error("This transaction must return a transaction-response Bundle that contains one entry " +
                "per entry in the request. Instead, a resource of type ${baseResource.class.simpleName} was returned.")
            }
            afterRun(baseResource)
        }
        testLog.add_name_value(instruction_output, 'InHeader', statusLine.toString())

        if (!content && fhirId) {
            reportManager.add("Type_ID", fhirId.withoutHistory())
            reportManager.add('Ref', "${endpoint}/${fhirId.withoutHistory()}")
        }
        if (!content) {
            if (statusLine.statusCode > 201) {
                stepContext.set_error(statusLine.reasonPhrase)
            }
            afterRun(null)
        }

//        if (statusLine.statusCode in 400..599)  {
//            stepContext.set_error("Status:${statusLine}")
//        }
//        reportManager.add("FhirIdWithHistory", fhirId.toString())
//        reportManager.add('RefWithHistory', "${endpoint}/${fhirId}")
    }

    void afterRun(IBaseResource returnedResource) {}

    List<String> resourceTypes(Bundle bundle) {
        def types = []

        bundle.entry.each { Bundle.BundleEntryComponent comp ->
            String type = comp.getResource().class.simpleName
            types << type
        }

        types
    }

    def simpleErrorMsg(OperationOutcome oo, StepContext sc) {
        assert oo
        def errs = []
        oo.issue.each { OperationOutcome.OperationOutcomeIssueComponent comp ->
            String diagnostics = comp.diagnostics
            String code = comp.codeElement?.value?.display
            String location = comp.location
            CodeableConcept details = comp.details
            String detailsStr
            if (details) {
                detailsStr = details.textElement
            }
            sc.set_error(detailsStr + '|' + code + '|' + diagnostics + '|' + location)
        }
    }

    @Override
    protected void parseInstruction(OMElement part) throws XdsInternalException, MetadataException {
        super.parseInstruction(part)
    }

    @Override
    protected String getBasicTransactionName() {
        return 'fhir'
    }

    def includeLocalReferences(IBaseResource resource) {
        def toAdd = []
        if (!(resource instanceof Bundle))
            return
        Bundle bundle = resource
        bundle.entry.each { Bundle.BundleEntryComponent comp ->
            Resource aResource = comp.getResource()
            if (aResource instanceof DocumentReference) {
                DocumentReference dr = aResource
                String binaryUrl = dr.content[0].attachment.url
                if (binaryUrl?.startsWith('file://')) {
                    String filename = binaryUrl.substring('file://'.size())
                    def (id, ext) = filename.split('\\.', 2)
                    Binary binary = new Binary()
                    File contentFile = new File(testConfig.testplanDir, filename)
                    binary.setContent(Io.bytesFromFile(contentFile))
                    binary.contentTypeElement = new CodeType(FhirSupport.mimeType(contentFile))
                    toAdd << [id, binary]
                    dr.content[0].attachment.url = id
                    dr.content[0].attachment.contentType = FhirSupport.mimeType(contentFile)
                }
            }
        }
        toAdd.each { String id, Resource r ->
            Bundle.BundleEntryComponent comp = new Bundle.BundleEntryComponent()
            comp.resource = r
            comp.id = id
            comp.fullUrl = id
            bundle.addEntry(comp)
        }
    }



}
