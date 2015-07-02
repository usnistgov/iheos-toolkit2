package gov.nist.toolkit.simulators.sim.reg;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.registrymsg.registry.Response;
import gov.nist.toolkit.simulators.support.DsSimCommon;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.simulators.support.TransactionSimulator;
import gov.nist.toolkit.valregmsg.registry.AdhocQueryResponse;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.xdsexception.XdsInternalException;


public class AdhocQueryResponseGenerator extends TransactionSimulator implements AdhocQueryResponseGeneratingSim{

	AdhocQueryResponseGeneratingSim querySim;
	AdhocQueryResponse ahqr;
	DsSimCommon dsSimCommon;
	
	public AdhocQueryResponseGenerator(SimCommon common, DsSimCommon dsSimCommon, AdhocQueryResponseGeneratingSim querySim) {
		super(common);
		this.dsSimCommon = dsSimCommon;
		this.querySim = querySim;
	}

	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		ahqr = querySim.getAdhocQueryResponse();
		try {
			ahqr.add(dsSimCommon.getRegistryErrorList(), null);
		} catch (XdsInternalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public AdhocQueryResponse getAdhocQueryResponse() {
		return ahqr;
	}

	public Response getResponse() {
		return ahqr;
	}

}
