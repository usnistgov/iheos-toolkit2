package gov.nist.toolkit.testengine.transactions

import gov.nist.toolkit.fhir.server.utility.FhirClient
import gov.nist.toolkit.fhir.server.utility.FhirId
import gov.nist.toolkit.testengine.engine.FhirContentFormat
import gov.nist.toolkit.testengine.engine.StepContext
import gov.nist.toolkit.testengine.engine.UniqueIdAllocator
import gov.nist.toolkit.testengine.fhir.FhirSupport
import gov.nist.toolkit.utilities.io.Io
import gov.nist.toolkit.xdsexception.client.MetadataException
import gov.nist.toolkit.xdsexception.client.XdsInternalException
import org.apache.axiom.om.OMElement
import org.apache.http.message.BasicStatusLine
import org.apache.log4j.Logger
import org.hl7.fhir.dstu3.model.*
import org.hl7.fhir.instance.model.api.IBaseResource

import javax.print.Doc

/**
 *
 */
class FhirCreateTransaction extends BasicFhirTransaction {
    static private final Logger logger = Logger.getLogger(FhirCreateTransaction.class);
    boolean addMasterIdentifier = false


    FhirCreateTransaction(StepContext s_ctx, OMElement instruction, OMElement instruction_output) {
        super(s_ctx, instruction, instruction_output)
    }

    def updateMasterIdentifier(def resource) {
        if ((resource instanceof DocumentManifest) || (resource instanceof DocumentReference)) {
            Identifier id = resource.getMasterIdentifier()
            id.value = 'urn:oid:' + UniqueIdAllocator.getInstance().allocate()
            resource.masterIdentifier = id
        } else if (resource instanceof Bundle) {
            Bundle bundle = resource
            bundle.entry.each { Bundle.BundleEntryComponent comp ->
                Resource res = comp.getResource()
                updateMasterIdentifier(res)
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

    static updateOriginalDocUrl(def resource, String symbolicReference, String url) {
        if (resource instanceof DocumentReference) {
            DocumentReference dr = resource
            dr.relatesTo.each { DocumentReference.DocumentReferenceRelatesToComponent comp ->
                if (comp.target.reference == symbolicReference)
                    comp.target = new Reference(url)
            }
        } else if (resource instanceof Bundle) {
            Bundle bundle = resource
            bundle.entry.each { Bundle.BundleEntryComponent comp ->
                Resource res = comp.getResource()
                updateOriginalDocUrl(res, symbolicReference, url)
            }
        }
    }

    static stripMetaFromContainedResources(Resource resource) {
        if (!resource)
            return
        if (resource instanceof Bundle) {
            Bundle b = (Bundle) resource
            b.entry.each { Bundle.BundleEntryComponent comp ->
                Resource r = comp.getResource()
                stripMetaFromContainedResources(r)
            }
        }
        if (resource instanceof DocumentManifest) {
            DocumentManifest dm = (DocumentManifest) resource
            dm.contained.each { Resource r ->
                stripMetaFromContainedResources(r)
            }
            dm.meta.versionId = null
            dm.meta.lastUpdated = null
            return
        }
        if (resource instanceof DocumentReference) {
            DocumentReference dm = (DocumentReference) resource
            dm.contained.each { Resource r ->
                stripMetaFromContainedResources(r)
            }
            dm.meta.versionId = null
            dm.meta.lastUpdated = null
            return
        }
        if (resource instanceof Practitioner) {
            Practitioner dm = (Practitioner) resource
            dm.contained.each { Resource r ->
                stripMetaFromContainedResources(r)
            }
            dm.meta.versionId = null
            dm.meta.lastUpdated = null
            return
        }
        if (resource instanceof Patient) {
            Patient dm = (Patient) resource
            dm.contained.each { Resource r ->
                stripMetaFromContainedResources(r)
            }
            dm.meta.versionId = null
            dm.meta.lastUpdated = null
            return
        }

    }

    @Override
    void doRun(IBaseResource resource, String urlExtension) {
        assert endpoint, 'TestClient:FhirCreateTransaction: endpoint is null'

        String patientReference = null
        Map<String, String> originalDocUrls = [:]

        stripMetaFromContainedResources(resource)

        if (useReportManager) {
            patientReference = useReportManager.get('$patient_reference$')

            (1..9).each { int index ->
                def url = useReportManager.get("docref${index}")
                if (url) {
                    originalDocUrls["docref${index}"] = url
                }
            }
        }

        includeLocalReferences(resource)

        if (patientReference)
            updatePatientReference(resource, patientReference)

        // assign new new masterIdentifier to all DocumentReference and Document Manifest objects
        if (addMasterIdentifier)
            updateMasterIdentifier(resource)

        if (originalDocUrls) {
            originalDocUrls.each { String symbol, String url ->
                updateOriginalDocUrl(resource, symbol, url)
            }
        }

        if (urlExtension && !urlExtension.startsWith('/'))
            urlExtension = "/${urlExtension}"

        // update endpoint to include urlExtension
        // the base variable has to be updated to it is logged to log.xml

        def fullEndpoint = "${endpoint}${urlExtension}"

        String base = null
        try {
            base = getBaseUrl()
        } catch (Exception e) {
            // ignore
        }
        reportManager.add('Base', base)   // endpoint)
        reportManager.add('Url', fullEndpoint)

        testLog.add_name_value(instruction_output, 'OutHeader', "POST ${fullEndpoint}")

        logger.info("Requested format ${transactionSettings.fhirContentFormat}")
        def sendContent
        if (transactionSettings.fhirContentFormat == FhirContentFormat.XML)
            sendContent = fhirCtx.newXmlParser().setPrettyPrint(true).encodeResourceToString(resource)
        else
            sendContent = fhirCtx.newJsonParser().setPrettyPrint(true).encodeResourceToString(resource)

        testLog.add_name_value(instruction_output, 'InputMetadata', sendContent)

        def dmCount = 0
        def drCount = 0
        def otherCount = 0

        // No fhirID from transaction
        def (BasicStatusLine statusLine, String content, FhirId fhirId, String error) = FhirClient.post(new URI(fullEndpoint), sendContent)
        if (error) stepContext.set_error(error)
        if (content) {
            IBaseResource baseResource = FhirSupport.parse(content)
            if (baseResource instanceof OperationOutcome) {
                OperationOutcome oo = (OperationOutcome) baseResource
                testLog.add_name_value(instruction_output, "Result", fhirCtx.newXmlParser().setPrettyPrint(true).encodeResourceToString(oo));
                simpleErrorMsg(oo, stepContext)
            } else if (baseResource instanceof Bundle) {
                testLog.add_name_value(instruction_output, "Result", fhirCtx.newXmlParser().setPrettyPrint(true).encodeResourceToString(baseResource));
                Bundle bundle = baseResource
                int eleCount=-1
                bundle.entry.each { Bundle.BundleEntryComponent comp ->
                    eleCount++
                    Bundle.BundleEntryResponseComponent resp = comp.response
                    if (!resp) {
                        stepContext.set_error("Return bundle entry #${eleCount} has no response element")
                        return
                    }
                    if (resp.status != '200')
                        stepContext.set_error("Response Bundle reported status of ${resp.status} (bundle.entry.response.status) for component (location) ${resp.location}")
                    def reportName
                    FhirId fhirId1 = new FhirId(resp.location)
                    def type = fhirId1.type
                    if (type == 'DocumentManifest') {
                        reportName = "DM${dmCount++}"
                    } else if (type == 'DocumentReference') {
                        reportName = "DR${drCount++}"
                    } else {
                        reportName = "OTHER${otherCount++}"
                    }
                    reportManager.add(reportName, resp.location)
                    reportManager.add("REF_${reportName}", resp.location)
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

    List<String> resourceTypesFromResponseLocations(Bundle bundle) {
        def types = []

        bundle.entry.each { Bundle.BundleEntryComponent comp ->
            String url = comp?.response?.location
            if (url) {
                FhirId fhirId = new FhirId(url)
                types << fhirId.type
            } else {
                types << 'None'
            }
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
        String part_name = part.getLocalName()

        if (part_name == 'AddMasterIdentifier')
            addMasterIdentifier = true
        else
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
                    if (!contentFile.exists()) {
                        // that was looking in the directory with the testplan.  If this is a utility
                        // it will be in a different directory
                        File dir = resourceFile.parentFile
                        contentFile = new File(dir, filename)
                        if (!contentFile.exists())
                            throw new Exception("Binary file reference from Resource (${filename}) cannot be found")
                    }
                    // resourceFile
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
