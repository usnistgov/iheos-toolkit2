package gov.nist.toolkit.simulators.sim.recip;

import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.TransactionType;
import gov.nist.toolkit.errorrecording.GwtErrorRecorderBuilder;
import gov.nist.toolkit.simulators.sim.reg.RegistryResponseGeneratorSim;
import gov.nist.toolkit.simulators.sim.reg.SoapWrapperRegistryResponseSim;
import gov.nist.toolkit.simulators.support.BaseDsActorSimulator;
import gov.nist.toolkit.simulators.support.DsSimCommon;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Deprecated  // Using RepositoryActorSimulator and RegistryActorSimulator via RepositoryRegistryActorFactory instead
public class RecipientActorSimulator extends BaseDsActorSimulator {

	public RecipientActorSimulator() {}

	public RecipientActorSimulator(SimCommon common, DsSimCommon dsSimCommon, SimDb db, SimulatorConfig simulatorConfig, HttpServletResponse response) {
		super(common, dsSimCommon);
		this.db = db;
		this.response = response;
		setSimulatorConfig(simulatorConfig);
	}

	public RecipientActorSimulator(DsSimCommon dsSimCommon, SimulatorConfig simulatorConfig) {
		super(dsSimCommon.simCommon, dsSimCommon);
		this.db = dsSimCommon.simCommon.db;
		this.response = dsSimCommon.simCommon.response;
        setSimulatorConfig(simulatorConfig);
		init();
	}

	public void init() {}

	public boolean run(TransactionType transactionType, MessageValidatorEngine mvc, String validation) throws IOException {
		GwtErrorRecorderBuilder gerb = new GwtErrorRecorderBuilder();
		
		if (transactionType.equals(TransactionType.XDR_PROVIDE_AND_REGISTER)) {
			
			common.vc.isPnR = true;
			common.vc.isXDR = true;
			common.vc.xds_b = true;
			common.vc.isRequest = true;
			common.vc.hasHttp = true;
			common.vc.hasSoap = true;
			
			if (getSimulatorConfig().getValidationContext()  != null) {
				common.vc.addInnerContext(getSimulatorConfig().getValidationContext());
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
