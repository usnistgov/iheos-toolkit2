package gov.nist.toolkit.testengine.transactions

import gov.nist.toolkit.fhir.server.utility.FhirClient
import gov.nist.toolkit.fhir.server.utility.FhirId
import gov.nist.toolkit.testengine.engine.StepContext
import gov.nist.toolkit.testengine.fhir.FhirSupport
import org.apache.axiom.om.OMElement
import org.apache.http.message.BasicStatusLine
import org.hl7.fhir.dstu3.model.*
import org.hl7.fhir.dstu3.model.codesystems.BundleType
import org.hl7.fhir.instance.model.api.IBaseResource

class ProvideDocumentBundleTransaction extends FhirCreateTransaction {
    String withPatientId = null
    Bundle sendResource

    ProvideDocumentBundleTransaction(StepContext s_ctx, OMElement instruction, OMElement instruction_output) {
        super(s_ctx, instruction, instruction_output)
    }

    @Override
    void doRun(IBaseResource resource, String urlExtension) {
        if (resource instanceof Bundle)
            sendResource = resource
        else {
            stepContext.set_error("Provide Document Bundle transaction cannot be run with resource type ${resource.class.simpleName} - a Bundle is required")
        }
        super.doRun(resource, urlExtension)
    }

    @Override
    void afterRun(IBaseResource returnResource) {
        if (returnResource == null) {
            stepContext.set_error('Provide Document Bundle transaction did not return bundle.')
            return
        }
        if (returnResource instanceof Bundle) {
            Bundle bundle = returnResource
            if (bundle.type.toCode() != BundleType.TRANSACTIONRESPONSE.toCode())
                err("Bundle type returned was ${bundle.type}, ${BundleType.TRANSACTIONRESPONSE} is required")
            def sendResourceTypes = resourceTypes(sendResource)
            def returnResourceTypes = resourceTypes(returnResource)
            if (sendResourceTypes != returnResourceTypes) {
                stepContext.with {
                    set_error("Provide Document Bundle shall return a Bundle containting one entry for each entry in the request.")
                    set_error("The reqeust bundle contained ${sendResourceTypes}")
                    set_error("The response bundle contained ${returnResourceTypes}")
                }
            }

            def sendIterator = sendResource.entry.iterator()
            def responseIterator = returnResource.entry.iterator()

            def sendEntry = sendIterator.next()
            def returnEntry = responseIterator.next()

            def fullUrls = []
            int index = 0
            boolean hasError = false
            while (sendEntry && returnEntry) {
                def responseEntry = returnEntry.response
                if (sendEntry.class.simpleName == returnEntry.class.simpleName) {
                    if (responseEntry.status != '200') {
                        hasError = true
                        err("Entry #${index} returned status ${responseEntry.status}")
                        if (responseEntry.outcome) {
                            if (responseEntry.outcome instanceof OperationOutcome)
                                FhirSupport.operationOutcomeIssues(responseEntry.outcome).each { err(it) }
                            else
                                err("Response Bundle entry #${index} outcome is of type ${responseEntry.outcome.class.simpleName}, type OperationOutcome is required")
                        }
                    }

                    Resource resource1 = returnEntry.getResource()
                    if ((resource1 instanceof DocumentReference) /*|| (resource1 instanceof DocumentManifest)  */) {
                        String url = returnEntry.fullUrl
                        if (!url.startsWith('http'))
                            err("Entry #${index} (${resource1.class.simpleName})in the return bundle does not have an absolute url in the fullUrl attribute - ${url} founc")
                        else
                            fullUrls << url
                    }

                    index++
                    if (sendIterator.hasNext() && responseIterator.hasNext()) {
                        sendEntry = sendIterator.next()
                        returnEntry = responseIterator.next()
                    } else
                        break
                }
            }
            if (hasError) {
                err("The reqeust bundle contained ${sendResourceTypes}")
                err("The response bundle contained ${returnResourceTypes}")

            }
            if (sendIterator.hasNext())
                err("Response has no entries for ${remainingResources(sendIterator)}")
            if (responseIterator.hasNext())
                err("Response has extra entries ${remainingResources(sendIterator)}")

            fullUrls.each { url ->
                try {
                    def (BasicStatusLine statusline, contentReturned) = FhirClient.get(url)
                    if (statusline.statusCode != 200)
                        err ("Cannot READ ${url} - a fullUrl returned in the response")
                    if (contentReturned) {
                        IBaseResource br = FhirSupport.parse(contentReturned)
                        String type = br.class.simpleName
                        FhirId fhirId = new FhirId(url)
                        String typeFromId = fhirId.type
                        if (type != typeFromId) {
                            err("Bundle entry #${index} has fullUrl ${url} but when READ from FHIR server a ${type} was returned")
                            if (type == 'OperationOutcome') {
                                err(FhirSupport.operationOutcomeIssues(br).join('\n'))
                            }
                        }
                    }
                } catch (Exception e) {
                    err("READ of ${url} failed (${e.getMessage()}) - this was the fullURL returned for one of the resources in the response bundle")
                }
            }

        } else {
            stepContext.set_error("Provide Document Bundle transaction must return bundle - returned ${returnResource.class.simpleName} instead")
            err(FhirSupport.operationOutcomeIssues(returnResource).join(']n'))
        }
    }

    static String additionalDocumentation() {
        '''
<h2>What is validated in the Provide Document Bundle response</h2>
<ul>
<li>bundle.type is transaction-response
<li>Same number of resources are returned in the Bundle as were sent
<li>The resource types returned match what was sent
<li>bundle.entry.status is 200 for each entry in the Bundle returned
<li>All DocumentReference resources returned have an absolute URL in the entry.fullUrl attribute
<li>For all these returned entry.fullUrls, a FHIR READ is used to get the contents and verify that the correct type of resource is returned
</ul>
'''
    }

    def remainingResources(Iterator iter) {
        def lst = []
        while (iter.hasNext())
            lst << iter.next()
        lst
    }

    def err(msg) { stepContext.set_error(msg) }

        @Override
    protected String getBasicTransactionName() {
        return 'pdb'
    }

}
