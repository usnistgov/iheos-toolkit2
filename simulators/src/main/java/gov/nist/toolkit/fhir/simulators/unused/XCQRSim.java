package gov.nist.toolkit.fhir.simulators.unused;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymsg.registry.Response;
import gov.nist.toolkit.fhir.simulators.sim.reg.RegistryResponseGeneratingSim;
import gov.nist.toolkit.fhir.simulators.support.MetadataGeneratingSim;
import gov.nist.toolkit.simcommon.server.SimCommon;
import gov.nist.toolkit.fhir.simulators.support.TransactionSimulator;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;



public class XCQRSim extends TransactionSimulator implements RegistryResponseGeneratingSim, MetadataGeneratingSim {
	Response response;
	Metadata m;
	
	public XCQRSim(SimCommon common, RegistryResponseGeneratingSim responseSim, MetadataGeneratingSim mSim) {
		super(common, null);

		response = responseSim.getResponse();
		m = mSim.getMetadata();
	}

	@Override
	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {

	}

	public Metadata getMetadata() {
		return m;
	}

	public Response getResponse() {
		return response;
	}

}
