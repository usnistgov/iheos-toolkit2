package gov.nist.toolkit.simulators.unused;

import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.simulators.sim.reg.RegRSim;
import gov.nist.toolkit.simulators.sim.rep.RepPnRSim;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.errrec.GwtErrorRecorderBuilder;
import gov.nist.toolkit.valsupport.message.MessageValidator;

/**
 * Handle XDR Recipient duties by storing document(s) in local Repository
 * and metadata in local Registry.
 * @author bill
 *
 */
public class RecipientPnRSim extends MessageValidator {
	SimCommon common;
	Exception startUpException = null;
	SimulatorConfig asc;

	public RecipientPnRSim(SimCommon common, SimulatorConfig asc) {
		super(common.vc);
		this.common = common;
		this.asc = asc;

		vc.hasSoap = true;
		vc.isPnR = true;
		vc.isRequest = true;
		vc.updateable = false;

	}


	public void run(ErrorRecorder er, MessageValidatorEngine mvc)  {
		this.er = er;

		if (startUpException != null)
			er.err(XdsErrorCode.Code.XDSRegistryError, startUpException);

		// if request didn't validation, return so errors can be reported
		if (common.hasErrors()) {
			return;
		}
		
		GwtErrorRecorderBuilder gerb = new GwtErrorRecorderBuilder();

		common.mvc.addMessageValidator("RepPnrSim", new RepPnRSim(common, asc), gerb.buildNewErrorRecorder());

		common.mvc.addMessageValidator("RegRSim", new RegRSim(common, asc), gerb.buildNewErrorRecorder());
		
		common.mvc.run();
		

	}



}
