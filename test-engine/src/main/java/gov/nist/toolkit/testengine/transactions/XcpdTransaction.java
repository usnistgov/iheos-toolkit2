package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.testengine.engine.StepContext;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.client.MetadataException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;

/**
 *
 */
public class XcpdTransaction  extends BasicTransaction {

    public XcpdTransaction(StepContext s_ctx, OMElement instruction, OMElement instruction_output) {
        super(s_ctx, instruction, instruction_output);
    }


    @Override
    protected void run(OMElement request) throws Exception {
        useAddressing = true;
        soap_1_2 = true;
        useMtom = false;
        noMetadataProcessing = true;
        assign_patient_id = false;
        try {
            soapCall(request);
            OMElement response = getSoapResult();
        } catch (Exception e) {
            throw new XdsInternalException(ExceptionUtil.exception_details(e));
        }
    }

    @Override
    protected void parseInstruction(OMElement part) throws XdsInternalException, MetadataException {
        parseBasicInstruction(part);
    }

    @Override
    protected String getRequestAction() {
        return "urn:hl7-org:v3:PRPA_IN201305UV02:CrossGatewayPatientDiscovery";
    }

    @Override
    protected String getBasicTransactionName() {
        return "xcpd";
    }
}
