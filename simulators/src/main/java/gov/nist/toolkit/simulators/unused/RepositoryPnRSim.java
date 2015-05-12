package gov.nist.toolkit.simulators.unused;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.simulators.sim.rep.RepPnRSim;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.errrec.GwtErrorRecorderBuilder;
import gov.nist.toolkit.valsupport.message.MessageValidator;

/**
 * Handle XDS Repository duties by storing document(s) in local Repository
 * and forwarding metadata to Registry.
 * @author bill
 *
 */

public class RepositoryPnRSim extends MessageValidator {
	SimCommon common;
	Exception startUpException = null;


	public RepositoryPnRSim(SimCommon common) {
		super(common.vc);
		this.common = common;

		vc.hasSoap = true;
		vc.isPnR = true;
		vc.isRequest = true;
		vc.updateable = false;

	}

	public void run(ErrorRecorder er, MessageValidatorEngine mvc)  {
		this.er = er;
		
		if (startUpException != null)
			er.err(XdsErrorCode.Code.XDSRegistryError, startUpException);

		// if request didn't validate, return so errors can be reported
		if (common.hasErrors()) {
			return;
		}
		
		GwtErrorRecorderBuilder gerb = new GwtErrorRecorderBuilder();

		common.mvc.addMessageValidator("RepPnrSim", new RepPnRSim(common, null), gerb.buildNewErrorRecorder());

//		Replace with something that will forward to registry
//		common.mvc.addMessageValidator("RegRSim", new RegRSim(common), gerb.buildNewErrorRecorder());
		
		common.mvc.run();
		

	}


}
