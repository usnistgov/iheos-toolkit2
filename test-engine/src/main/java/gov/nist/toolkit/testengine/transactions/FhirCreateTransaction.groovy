package gov.nist.toolkit.testengine.transactions

import ca.uhn.fhir.context.FhirContext
import gov.nist.toolkit.installation.ResourceCache
import gov.nist.toolkit.testengine.engine.StepContext
import gov.nist.toolkit.testengine.fhir.FhirClient
import gov.nist.toolkit.testengine.fhir.FhirId
import gov.nist.toolkit.xdsexception.client.MetadataException
import gov.nist.toolkit.xdsexception.client.XdsInternalException
import org.apache.axiom.om.OMElement
import org.apache.http.message.BasicStatusLine
import org.hl7.fhir.dstu3.model.Bundle
import org.hl7.fhir.dstu3.model.CodeableConcept
import org.hl7.fhir.dstu3.model.OperationOutcome
import org.hl7.fhir.instance.model.api.IBaseResource
/**
 *
 */
class FhirCreateTransaction extends BasicFhirTransaction {
    FhirCreateTransaction(StepContext s_ctx, OMElement instruction, OMElement instruction_output) {
        super(s_ctx, instruction, instruction_output)
    }

    @Override
    void doRun(IBaseResource resource, String urlExtension) {
        assert endpoint, 'TestClient:FhirCreateTransaction: endpoint is null'

        if (urlExtension && !urlExtension.startsWith('/'))
            urlExtension = "/${urlExtension}"

        // update endpoint to include urlExtension
        // the base variable has to be updated to it is logged to log.xml

        def fullEndpoint = "${endpoint}${urlExtension}"

        reportManager.add('Base', endpoint)
        reportManager.add('Url', fullEndpoint)

//        fullEndpoint = fullEndpoint.replace('7777', '6666')

        def (BasicStatusLine statusLine, String content, FhirId fhirId) = FhirClient.post(new URI(fullEndpoint), fhirCtx.newJsonParser().encodeResourceToString(resource))
        if (content) {
            if (content instanceof OperationOutcome) {
                OperationOutcome oo = (OperationOutcome) parse(content)

                stepContext.set_error(simpleErrorMsg(oo))
            } else if (content instanceof Bundle) {
                Bundle bundle = content
                bundle.entry.each { Bundle.BundleEntryComponent comp ->
                    assert comp.response.status == '200'
                    if (comp.fullUrl) {
                        reportManager.add('Ref', new FhirId(comp.fullUrl).withoutHistory())
                    }
                    FhirId myId = new FhirId(comp.response?.outcome?.id)
                    if (myId)
                        reportManager.add('Ref', myId.withoutHistory())
                }
            }
        }
        if (!content && fhirId) {
            reportManager.add("Type_ID", fhirId.withoutHistory())
            reportManager.add('Ref', "${endpoint}/${fhirId.withoutHistory()}")
        }

//        if (statusLine.statusCode in 400..599)  {
//            stepContext.set_error("Status:${statusLine}")
//        }
//        reportManager.add("FhirIdWithHistory", fhirId.toString())
//        reportManager.add('RefWithHistory', "${endpoint}/${fhirId}")
    }

    def simpleErrorMsg(OperationOutcome oo) {
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
            errs << detailsStr + '|' + code + '|' + diagnostics + '|' + location
        }
        return errs.join('\n')
    }

    @Override
    protected void parseInstruction(OMElement part) throws XdsInternalException, MetadataException {
        super.parseInstruction(part)
    }

    @Override
    protected String getBasicTransactionName() {
        return 'fhir'
    }

    IBaseResource parse(String content) {
        FhirContext ctx = ResourceCache.ctx
        content = content.trim()
        if (content.startsWith('{')) {
            return ctx.newJsonParser().parseResource(content)
        } else {
            return ctx.newXmlParser().parseResource(content)
        }
        return null
    }

}
