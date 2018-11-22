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
            Bundle returnBundle = returnResource

            // 3.65.4.2.2 Message Semantics
            if (returnBundle?.type?.toCode() != BundleType.TRANSACTIONRESPONSE.toCode())
                err("Bundle type returned was ${returnBundle.type}, ${BundleType.TRANSACTIONRESPONSE} is required")

            // 3.65.4.2.2 Message Semantics
            // ...that contains one entry for each entry in the request, in the same order as received
            def sendResourceTypes = resourceTypes(sendResource)
            def returnResourceTypes = resourceTypesFromResponseLocations(returnResource)
            if (sendResourceTypes == returnResourceTypes) {
                stepContext.addDetail("Bundle contents", sendResourceTypes.toString())
            } else {
                stepContext.with {
                    set_error("Provide Document Bundle shall return a Bundle containting one entry for each entry in the request.")
                    set_error("The reqeust bundle contained ${sendResourceTypes}")
                    set_error("The response bundle contained ${returnResourceTypes}")
                }
            }

            def sendIterator = sendResource.entry.iterator()
            def responseIterator = returnBundle.entry.iterator()

            def fullUrlsReturned = []
            int index = 0
            boolean hasError = false

            def sendEntry = sendIterator.next()
            def returnEntry = responseIterator.next()
            while (sendEntry && returnEntry) {
                def responseEntry = returnEntry.response
                if (sendEntry.class.simpleName == returnEntry.class.simpleName) {
                    if (responseEntry.status == '201') {
                        if (responseEntry.outcome) {
                            hasError = true
                            err("Entry #${index} (${sendEntry.class.simpleName}) returned status 201 and a response outcome:...")
                            FhirSupport.operationOutcomeIssues(responseEntry.outcome).each { err(it) }
                        }
                    } else {
                        hasError = true
                        err("Entry #${index} (${sendEntry.class.simpleName}) returned status ${responseEntry.status}")
                        if (responseEntry.outcome) {
                            // http://hl7.org/fhir/STU3/http.html#transaction-response
                            // For a failed transaction, the server returns a single OperationOutcome instead of a Bundle
                            if (responseEntry.outcome instanceof OperationOutcome) {
                                err("Response Bundle returned OperationOutcome for an entry - For a failed transaction, the server returns a single OperationOutcome instead of a Bundle - http://hl7.org/fhir/STU3/http.html#transaction-response")
                                FhirSupport.operationOutcomeIssues(responseEntry.outcome).each { err(it) }
                            }
                        }
                    }

                    Resource resource1 = returnEntry.getResource()
                    if ((resource1 instanceof DocumentReference) /*|| (resource1 instanceof DocumentManifest)  */) {
                        String url = returnEntry.response.location
                        if (!url) {
                            err("Response Bundle entry #${index} did not return a response.location")
                        }
                        else if (!url.startsWith('http'))
                            err("Entry #${index} (${resource1.class.simpleName})in the return bundle does not have an absolute url in response.location - ${url} found")
                        else
                            fullUrlsReturned << url
                    }

                    index++
                    if (sendIterator.hasNext() && responseIterator.hasNext()) {
                        sendEntry = sendIterator.next()
                        returnEntry = responseIterator.next()
                    } else
                        break
                }
            }
            if (sendIterator.hasNext())
                err("Response has no entries for ${remainingResources(sendIterator)}")
            if (responseIterator.hasNext())
                err("Response has extra entries ${remainingResources(sendIterator)}")

            // verify urls returened by READing each one
            index = 0
            fullUrlsReturned.each { url ->
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
                            err("Bundle entry #${index} has response.location ${url} but when READ from FHIR server a ${type} was returned")
                            if (type == 'OperationOutcome') {
                                err(FhirSupport.operationOutcomeIssues(br).join('\n'))
                            }
                        }
                    } else {
                        err("READ of ${url} returned no content")
                    }
                } catch (Exception e) {
                    err("READ of ${url} failed (${e.getMessage()}) - this was the response.location returned for one of the resources in the response bundle")
                }
                index++
            }

        } else {
            stepContext.set_error("Provide Document Bundle transaction must return bundle - returned ${returnResource.class.simpleName} instead")
            if (returnResource instanceof OperationOutcome) {
                testLog.add_name_value(instruction_output, "Result", fhirCtx.newXmlParser().setPrettyPrint(true).encodeResourceToString(returnResource));

                err(FhirSupport.operationOutcomeIssues(returnResource).join(']n'))
            }
        }
    }

    static String additionalDocumentation() {
        '''
<h2>What is validated in the Provide Document Bundle response</h2>
<ul>
<li>bundle.type is transaction-response
<li>Same number of resources are returned in the Bundle as were sent
<li>The resource types returned match what was sent
<li>bundle.entry.response.status is 201 for each entry in the Bundle returned
<li>All DocumentReference resources returned have an absolute URL in the bundle.entry.response.location attribute
<li>For all these returned bundle.entry.response.location's, a FHIR READ is used to verify the resource is available at that location
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
