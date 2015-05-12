package gov.nist.toolkit.simulators.sim.recip;

import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ATFactory.TransactionType;
import gov.nist.toolkit.simulators.sim.reg.RegistryResponseGeneratorSim;
import gov.nist.toolkit.simulators.sim.reg.SoapWrapperRegistryResponseSim;
import gov.nist.toolkit.simulators.support.ActorSimulator;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.errrec.GwtErrorRecorderBuilder;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public class RecipientActorSimulator extends ActorSimulator {
	SimDb db;
	HttpServletResponse response;
	SimulatorConfig asc;
	
	public RecipientActorSimulator(SimCommon common, SimDb db, SimulatorConfig asc, HttpServletResponse response) {
		super(common);
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
			
			if (!common.runInitialValidations())
				return false;
			
			if (mvc.hasErrors()) {
				common.sendErrorsInRegistryResponse(er);
				return false;
			}
			
			RegistryResponseGeneratorSim rrg = new RegistryResponseGeneratorSim(common);
			
			mvc.addMessageValidator("Attach Errors", rrg, gerb.buildNewErrorRecorder());
						
			// wrap in soap wrapper and http wrapper
			// auto-detects need for multipart/MTOM
			mvc.addMessageValidator("ResponseInSoapWrapper", new SoapWrapperRegistryResponseSim(common, rrg), gerb.buildNewErrorRecorder());
			
			mvc.run();
			
			return true;
			
		}
		else {
			common.sendFault("Don't understand transaction " + transactionType, null);
			return false;
		} 


	}

}
