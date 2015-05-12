package gov.nist.toolkit.simulators.sim.reg;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.registrymsg.registry.Response;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.simulators.support.TransactionSimulator;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.xdsexception.ExceptionUtil;

import org.apache.axiom.om.OMElement;


public class SoapWrapperRegistryResponseSim extends TransactionSimulator {
	RegistryResponseGeneratingSim rrSim;
	Response response;
	
	public SoapWrapperRegistryResponseSim(SimCommon common, RegistryResponseGeneratingSim rrSim) {
		super(common);
		this.rrSim = rrSim;
	}

	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		this.response = rrSim.getResponse();
		try {

//			response.add(common.getRegistryErrorList(), null);

			er.detail("Wrapping response in SOAP Message");
			OMElement env = common.wrapResponseInSoapEnvelope(response.getResponse());
//			OMElement env = common.wrapResponseInSoapEnvelope(response.getRoot());


			common.sendHttpResponse(env, er);  
		}
		// this cannot be - registry errors already sealed - this must be SOAP Fault
		catch (Exception e) {
			er.err(XdsErrorCode.Code.SoapFault, e);
			common.sendFault("Error wrapping response in Soap Envelope", e);
			System.out.println(ExceptionUtil.exception_details(e));
		} 
	}

}
