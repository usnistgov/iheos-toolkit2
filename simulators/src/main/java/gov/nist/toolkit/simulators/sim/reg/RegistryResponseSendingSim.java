package gov.nist.toolkit.simulators.sim.reg;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.simulators.support.TransactionSimulator;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.errrec.GwtErrorRecorderBuilder;

/**
 * Take errors recorded in MessageValidationEngine, package them in a RegistryResponse and send.
 * @author bill
 *
 */
public class RegistryResponseSendingSim extends TransactionSimulator {
	
	public RegistryResponseSendingSim(SimCommon common) {
		super(common);
	}

	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		RegistryResponseGeneratorSim registryResponseGenerator = new RegistryResponseGeneratorSim(common);
		mvc.addMessageValidator("Attach Errors", registryResponseGenerator, er);
		mvc.addMessageValidator("SendResponseInSoapWrapper", 
				new SoapWrapperRegistryResponseSim(common, registryResponseGenerator), // wrap in SOAP and HTTP and send
				new GwtErrorRecorderBuilder().buildNewErrorRecorder()   // if this ErrorRecorder catches something it will go in the logs but not sent
				);
		
		mvc.run();

	}

}
