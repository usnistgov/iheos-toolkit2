package gov.nist.toolkit.simulators.sim.reg.store;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.AbstractMessageValidator;

// This commits delta into in-memory store
public class Committer extends AbstractMessageValidator {
	SimCommon common;
	MetadataCollection delta;
	
	public Committer(SimCommon common, MetadataCollection delta) {
		super(common.vc);
		this.common = common;
		this.delta = delta;
	}

	// caller takes responsibility for locking

	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		this.er = er;
		er.registerValidator(this);
		

		// merge in changes
		delta.mergeDelta(er);
		er.unRegisterValidator(this);
	}

}
