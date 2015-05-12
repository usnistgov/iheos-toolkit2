package gov.nist.toolkit.simulators.unused;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.simulators.support.TransactionSimulator;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.xdsexception.ExceptionUtil;

import org.apache.axiom.om.OMElement;


public class SoapWrapperXCPDResponseSim extends TransactionSimulator {
	OMElement response;

	
	public SoapWrapperXCPDResponseSim(SimCommon common, OMElement xcpdResponseGenerator) {
		super(common);
	}

	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		try {

			er.detail("Wrapping response in SOAP Message");
			OMElement env = common.wrapResponseInSoapEnvelope(response);

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
