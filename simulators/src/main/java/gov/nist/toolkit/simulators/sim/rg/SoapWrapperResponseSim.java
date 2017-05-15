package gov.nist.toolkit.simulators.sim.rg;

import gov.nist.toolkit.errorrecording.IErrorRecorder;
import gov.nist.toolkit.errorrecording.common.XdsErrorCode;
import gov.nist.toolkit.simulators.support.DsSimCommon;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.simulators.support.TransactionSimulator;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.xdsexception.ExceptionUtil;

import org.apache.axiom.om.OMElement;

public class SoapWrapperResponseSim extends TransactionSimulator {
	OMElement response;
	DsSimCommon dsSimCommon;

	public SoapWrapperResponseSim(SimCommon common, DsSimCommon dsSimCommon, OMElement response) {
		super(common, null);
		this.dsSimCommon = dsSimCommon;
		this.response = response;
	}

	public void run(IErrorRecorder er, MessageValidatorEngine mvc) {
		try {

			er.detail("Wrapping response in SOAP Message");
			OMElement env = dsSimCommon.wrapResponseInSoapEnvelope(response);

			dsSimCommon.sendHttpResponse(env, er);
		}
		
		// this cannot be - registry errors already sealed - this must be SOAP Fault
		// actually, need better answer than this
		catch (Exception e) {
			er.err(XdsErrorCode.Code.SoapFault, e);
			dsSimCommon.sendFault("Error wrapping response in Soap Envelope", e);
			System.out.println(ExceptionUtil.exception_details(e));
		} 
	}

}
