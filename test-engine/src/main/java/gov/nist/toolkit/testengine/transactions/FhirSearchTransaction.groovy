package gov.nist.toolkit.testengine.transactions

import gov.nist.toolkit.fhir.server.utility.FhirClient
import gov.nist.toolkit.fhir.server.utility.UriBuilder
import gov.nist.toolkit.testengine.engine.StepContext
import gov.nist.toolkit.testengine.fhir.FhirSupport
import gov.nist.toolkit.xdsexception.client.MetadataException
import gov.nist.toolkit.xdsexception.client.XdsInternalException
import org.apache.axiom.om.OMElement
import org.apache.http.message.BasicStatusLine
import org.hl7.fhir.dstu3.model.*
import org.hl7.fhir.instance.model.api.IBaseResource

import javax.xml.namespace.QName

/**
 *
 */
class FhirSearchTransaction extends BasicFhirTransaction {
    boolean requestXml = false
    ExpectedContent ec = null

    FhirSearchTransaction(StepContext s_ctx, OMElement instruction, OMElement instruction_output) {
        super(s_ctx, instruction, instruction_output)
    }

    /**
     *
     * @param resource - always null
     * @param urlExtension
     */
    @Override
    void doRun(IBaseResource resource, String urlExtension) {
        def base = useReportManager.get('Base')
        assert base, 'FhirSearchTransaction - UseReport Base_Type is null'
        assert queryParams, 'FhirSearchTransaction - UseReport QueryParams is null'

        def fullEndpoint = "${base}${queryParams}"

        reportManager.add('Url', fullEndpoint)

        testLog.add_name_value(instruction_output, 'OutHeader', "GET ${fullEndpoint}")

        def contentType = (requestXml) ? 'application/fhir+xml' : 'application/fhir+json'
        def (BasicStatusLine statusLine, String content) = FhirClient.get(UriBuilder.build(fullEndpoint), contentType)
        if (statusLine.statusCode in 400..599)  {
            stepContext.set_error("Status:${statusLine}")
        }

        testLog.add_name_value(instruction_output, "Result", content);

        if (content) {
            IBaseResource res = FhirSupport.parse(content)
            if (res instanceof Patient) {
                Patient patient = res
                Identifier ident = patient.getIdentifier()?.get(0)
                if (ident) {
                    def value = ident.value
                    def system = ident.system
                    if (value && system) {
                        reportManager.add('PatientId', "${value}^^^&${system}&ISO")
                    }
                }
            } else if (res instanceof OperationOutcome) {
                OperationOutcome oo = res
                oo.issue.each { OperationOutcome.OperationOutcomeIssueComponent comp ->
                    stepContext.set_error(comp.diagnostics)
                }
            } else if (ec) {
                int drCount = 0
                int dmCount = 0

                if (res instanceof Bundle) {
                    res.entry.each { Bundle.BundleEntryComponent comp ->
                        def resour = comp.getResource()
                        if (resour instanceof DocumentReference)
                            drCount++
                        if (resour instanceof DocumentManifest)
                            dmCount++
                    }
                }
                if (drCount != ec.documentReferenceCount)
                    stepContext.set_error("Expected ${ec.documentReferenceCount} DocumentReference resources in result, got ${drCount} instead")
                if (dmCount != ec.documentManifestCount)
                    stepContext.set_error("Expected ${ec.documentManifestCount} DocumentManifest resources in result, got ${dmCount} instead")
            }
        }
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
        else if (part_name == 'ExpectedContent') {
            ec = new ExpectedContent()
            def dr = part.getAttribute(new QName('dr')).attributeValue
            def dm = part.getAttribute(new QName('dm')).attributeValue
            ec.documentReferenceCount = Integer.parseInt(dr)
            ec.documentManifestCount = Integer.parseInt(dm)
        }
        else {
            super.parseInstruction(part)
        }
    }

    class ExpectedContent {
        def documentReferenceCount
        def documentManifestCount
    }

}
