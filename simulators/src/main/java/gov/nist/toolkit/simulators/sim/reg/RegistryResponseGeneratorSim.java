package gov.nist.toolkit.simulators.sim.reg;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.registrymsg.registry.Response;
import gov.nist.toolkit.simulators.support.DsSimCommon;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.simulators.support.TransactionSimulator;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.xdsexception.XdsInternalException;


public class RegistryResponseGeneratorSim extends TransactionSimulator implements RegistryResponseGeneratingSim {
	DsSimCommon dsSimCommon;
	Response response = null;
	Exception startUpException = null;


	public RegistryResponseGeneratorSim(SimCommon common, DsSimCommon dsSimCommon) {
		super(common, null);
		this.dsSimCommon = dsSimCommon;
	}

	public Response getResponse() {
		return response;
	}

	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		try {
			response = dsSimCommon.getRegistryResponse();
		} catch (XdsInternalException e) {
			e.printStackTrace();
		}
	}

}
