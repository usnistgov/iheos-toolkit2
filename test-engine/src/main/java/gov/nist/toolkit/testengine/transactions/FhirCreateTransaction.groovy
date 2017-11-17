package gov.nist.toolkit.testengine.transactions

import gov.nist.toolkit.fhir.utility.FhirClient
import gov.nist.toolkit.fhir.utility.FhirId
import gov.nist.toolkit.testengine.engine.StepContext
import gov.nist.toolkit.testengine.engine.UniqueIdAllocator
import gov.nist.toolkit.testengine.fhir.FhirSupport
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

    @Override
    void doRun(IBaseResource resource, String urlExtension) {
        assert endpoint, 'TestClient:FhirCreateTransaction: endpoint is null'

        String pid_value = null
        String pid_system = null

        if (useReportManager) {
            pid_value = useReportManager.get('$pid_value$');
            pid_system = useReportManager.get('$pid_system$');
        }

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

        reportManager.add('Base', endpoint)
        reportManager.add('Url', fullEndpoint)

//        fullEndpoint = fullEndpoint.replace('7777', '6666')

        // No fhirID from transaction
        def (BasicStatusLine statusLine, String content, FhirId fhirId) = FhirClient.post(new URI(fullEndpoint), fhirCtx.newJsonParser().encodeResourceToString(resource))
        if (content) {
            IBaseResource baseResource = FhirSupport.parse(content)
            if (baseResource instanceof OperationOutcome) {
                OperationOutcome oo = (OperationOutcome) baseResource

                simpleErrorMsg(oo, stepContext)
            } else if (baseResource instanceof Bundle) {
                Bundle bundle = baseResource
                bundle.entry.each { Bundle.BundleEntryComponent comp ->
                    assert comp.response.status == '200'
                    if (comp.fullUrl) {
                        reportManager.add('Ref', new FhirId(comp.fullUrl).withoutHistory())
                    }
//                    FhirId myId = new FhirId(comp.response?.outcome?.id)
//                    if (myId)
//                        reportManager.add('Ref', myId.withoutHistory())
                }
            }
        }
        if (!content && fhirId) {
            reportManager.add("Type_ID", fhirId.withoutHistory())
            reportManager.add('Ref', "${endpoint}/${fhirId.withoutHistory()}")
        }
        if (!content) {
            if (statusLine.statusCode > 201) {
                stepContext.set_error(statusLine.reasonPhrase)
            }
        }

//        if (statusLine.statusCode in 400..599)  {
//            stepContext.set_error("Status:${statusLine}")
//        }
//        reportManager.add("FhirIdWithHistory", fhirId.toString())
//        reportManager.add('RefWithHistory', "${endpoint}/${fhirId}")
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


}
