package gov.nist.toolkit.simulators.support;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.registrymsg.registry.RegistryResponse;
import gov.nist.toolkit.registrymsg.registry.Response;
import gov.nist.toolkit.simulators.sim.reg.RegistryResponseGeneratingSim;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.xdsexception.ExceptionUtil;

public class UnknownSim extends TransactionSimulator implements RegistryResponseGeneratingSim  {
	Response response;
	Exception startUpException = null;

	public UnknownSim(SimCommon common) {
		super(common, null);

		// build response
		try {
			response = new RegistryResponse(Response.version_3);
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e));
			startUpException = e;
			return;
		}
	}

	public void run(ErrorRecorder er, MessageValidatorEngine mve) {
		if (startUpException != null)
			er.err(Code.XDSRegistryError, startUpException);
	}

	public Response getResponse() {
		return null;
	}

}
