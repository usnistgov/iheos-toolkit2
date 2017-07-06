package gov.nist.toolkit.testengine.transactions

import gov.nist.toolkit.testengine.engine.StepContext
import gov.nist.toolkit.xdsexception.client.MetadataException
import gov.nist.toolkit.xdsexception.client.XdsInternalException
import org.apache.axiom.om.OMElement
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
        doPost(resource, urlExtension)
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
