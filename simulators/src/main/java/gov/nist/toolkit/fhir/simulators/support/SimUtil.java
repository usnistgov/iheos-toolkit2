package gov.nist.toolkit.fhir.simulators.support;


import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.validatorsSoapMessage.message.StsSamlValidator;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.message.AbstractMessageValidator;
import org.apache.axiom.om.OMElement;

public class SimUtil {
	
//	static public RetrieveDocumentSetResponse getRetrieveDocumentSetResponse() throws XdsInternalException {
//		RegistryResponse rr = SimUtil.getRegistryResponse(new ArrayList<ValidationStepResult>());
//		RetrieveDocumentSetResponse rdsr = new RetrieveDocumentSetResponse(rr);
//		return rdsr;
//	}
	
//	static public AdhocQueryResponse getAdhocQueryResponse(List<OMElement> contents) throws XdsInternalException {
//		AdhocQueryResponse resp = new AdhocQueryResponse(Response.version_3);
//		
//		resp.addQueryResults(contents);
//		
//		return resp;
//	}


    /**
     * @param vc
     * @param dsSimCommon
     * @param className The class name
     * @return
     */
    public static OMElement getSecurityElement(ValidationContext vc, DsSimCommon dsSimCommon, String className) {
            AbstractMessageValidator mv = dsSimCommon.getMessageValidatorIfAvailable(StsSamlValidator.class);
            if (mv == null || !(mv instanceof StsSamlValidator)) {
                dsSimCommon.er.err(XdsErrorCode.Code.XDSRegistryError, "Internal Error - cannot find StsSamlValidator instance", className, "");
                dsSimCommon.sendErrorsInAdhocQueryResponse(dsSimCommon.er);
            }
            StsSamlValidator sv = (StsSamlValidator) mv;
            return sv.getSecurityElement();
    }

}
