package gov.nist.toolkit.simulators.sim.reg;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.simulators.support.DsSimCommon;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.simulators.support.TransactionSimulator;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.errorrecording.gwt.GwtErrorRecorderBuilder;

/**
 * Take errors recorded in MessageValidationEngine, package them in a RegistryResponse and send.
 * @author bill
 *
 */
public class RegistryResponseSendingSim extends TransactionSimulator {
	DsSimCommon dsSimCommon;

	public RegistryResponseSendingSim(SimCommon common, DsSimCommon dsSimCommon) {
        super(common, null);
        this.dsSimCommon = dsSimCommon;
	}

	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		RegistryResponseGeneratorSim registryResponseGenerator = new RegistryResponseGeneratorSim(common, dsSimCommon);
		mvc.addMessageValidator("Attach Errors", registryResponseGenerator, er);
		mvc.addMessageValidator("SendResponseInSoapWrapper", 
				new SoapWrapperRegistryResponseSim(common, dsSimCommon, registryResponseGenerator), // wrap in SOAP and HTTP and send
				new GwtErrorRecorderBuilder().buildNewErrorRecorder()   // if this ErrorRecorder catches something it will go in the logs but not sent
				);
		
		mvc.run();

	}

}
