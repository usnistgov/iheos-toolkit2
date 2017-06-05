package gov.nist.toolkit.simulators.unused;

import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.errorrecording.IErrorRecorder;
import gov.nist.toolkit.errorrecording.common.ErrorRecorderFactory;
import gov.nist.toolkit.errorrecording.common.XdsErrorCode;
import gov.nist.toolkit.simulators.sim.reg.RegRSim;
import gov.nist.toolkit.simulators.sim.rep.RepPnRSim;
import gov.nist.toolkit.simulators.support.DsSimCommon;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.errorrecording.gwt.GwtErrorRecorderBuilder;
import gov.nist.toolkit.valsupport.message.AbstractMessageValidator;

/**
 * Handle XDR Recipient duties by storing document(s) in local Repository
 * and metadata in local Registry.
 * @author bill
 *
 */
public class RecipientPnRSim extends AbstractMessageValidator {
	DsSimCommon dsSimCommon;
	SimCommon common;
	Exception startUpException = null;
	SimulatorConfig asc;

	public RecipientPnRSim(SimCommon common, DsSimCommon dsSimCommon, SimulatorConfig asc) {
		super(common.vc);
		this.common = common;
        this.dsSimCommon = dsSimCommon;
		this.asc = asc;

		vc.hasSoap = true;
		vc.isPnR = true;
		vc.isRequest = true;
		vc.updateable = false;

	}


	public void run(IErrorRecorder er, MessageValidatorEngine mvc)  {
		this.er = er;

		if (startUpException != null)
			er.err(XdsErrorCode.Code.XDSRegistryError, startUpException);

		// if request didn't validation, return so errors can be reported
		if (common.hasErrors()) {
			return;
		}
		
		GwtErrorRecorderBuilder gerb = new GwtErrorRecorderBuilder();

		common.mvc.addMessageValidator("RepPnrSim", new RepPnRSim(common, dsSimCommon, asc), ErrorRecorderFactory.getErrorRecorderFactory().getNewErrorRecorder());

		common.mvc.addMessageValidator("RegRSim", new RegRSim(common, dsSimCommon, asc), ErrorRecorderFactory.getErrorRecorderFactory().getNewErrorRecorder());
		
		common.mvc.run();
	}

}
