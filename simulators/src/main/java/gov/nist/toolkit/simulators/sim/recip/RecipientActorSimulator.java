package gov.nist.toolkit.simulators.sim.recip;

import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ATFactory.TransactionType;
import gov.nist.toolkit.simulators.sim.reg.RegistryResponseGeneratorSim;
import gov.nist.toolkit.simulators.sim.reg.SoapWrapperRegistryResponseSim;
import gov.nist.toolkit.simulators.support.DsActorSimulator;
import gov.nist.toolkit.simulators.support.DsSimCommon;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.errorrecording.GwtErrorRecorderBuilder;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public class RecipientActorSimulator extends DsActorSimulator {
	DsSimCommon dsSimCommon;
	SimDb db;
	HttpServletResponse response;
	SimulatorConfig asc;
	
	public RecipientActorSimulator(SimCommon common, DsSimCommon dsSimCommon, SimDb db, SimulatorConfig asc, HttpServletResponse response) {
		super(common, dsSimCommon);
		this.db = db;
		this.response = response;
		this.asc = asc;
	}
	 
	public boolean run(TransactionType transactionType, MessageValidatorEngine mvc, String validation) throws IOException {
		GwtErrorRecorderBuilder gerb = new GwtErrorRecorderBuilder();
		
		if (transactionType.equals(TransactionType.XDR_PROVIDE_AND_REGISTER)) {
			
			common.vc.isPnR = true;
			common.vc.isXDR = true;
//			common.vc.isXDRLimited = true;;
			common.vc.xds_b = true;
			common.vc.isRequest = true;
			common.vc.hasHttp = true;
			common.vc.hasSoap = true;
			
			if (asc.getValidationContext()  != null) {
				common.vc.addInnerContext(asc.getValidationContext());
			}
			
			if (!dsSimCommon.runInitialValidationsAndFaultIfNecessary())
				return false;
			
			if (mvc.hasErrors()) {
                dsSimCommon.sendErrorsInRegistryResponse(er);
				return false;
			}
			
			RegistryResponseGeneratorSim rrg = new RegistryResponseGeneratorSim(common, dsSimCommon);
			
			mvc.addMessageValidator("Attach Errors", rrg, gerb.buildNewErrorRecorder());
						
			// wrap in soap wrapper and http wrapper
			// auto-detects need for multipart/MTOM
			mvc.addMessageValidator("ResponseInSoapWrapper", new SoapWrapperRegistryResponseSim(common, dsSimCommon, rrg), gerb.buildNewErrorRecorder());
			
			mvc.run();
			
			return true;
			
		}
		else {
            dsSimCommon.sendFault("Don't understand transaction " + transactionType, null);
			return false;
		} 


	}

}
