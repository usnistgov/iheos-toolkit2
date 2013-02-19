package gov.nist.toolkit.simulators.sim.ig;


import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ATFactory.TransactionType;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.simulators.sim.reg.AdhocQueryResponseGenerator;
import gov.nist.toolkit.simulators.sim.reg.SoapWrapperRegistryResponseSim;
import gov.nist.toolkit.simulators.support.GatewaySimulatorCommon;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.valregmsg.message.SoapMessageValidator;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.MessageValidator;

import java.io.IOException;

import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

public class IgActorSimulator extends GatewaySimulatorCommon {
	SimDb db;
	SimulatorConfig asc;
	OMElement messageBody;
	static Logger logger = Logger.getLogger(IgActorSimulator.class);
	AdhocQueryResponseGenerator sqs;

	public IgActorSimulator(SimCommon common, SimDb db, SimulatorConfig asc) {
		super(common);
		this.db = db;
		this.asc = asc;
	}


	// boolean => hasErrors?
	public boolean run(TransactionType transactionType, MessageValidatorEngine mvc, String validationPattern) throws IOException {

		if (transactionType.equals(TransactionType.IG_QUERY.getShortName())) {

			common.vc.isSQ = true;
			common.vc.isXC = false;
			common.vc.isRequest = true;
			common.vc.isSimpleSoap = true;
			common.vc.hasSoap = true;
			common.vc.hasHttp = true;


			
			if (!common.runInitialValidations())
				return false;
			
			if (mvc.hasErrors()) {
				common.sendErrorsInRegistryResponse(er);
				return false;
			}
			
			// extract query 
			MessageValidator mv = common.getMessageValidator(SoapMessageValidator.class);
			if (mv == null || !(mv instanceof SoapMessageValidator)) {
				er.err(Code.XDSRegistryError, "IG Internal Error - cannot find SoapMessageValidator instance", "InitiatingGatewayActorSimulator", "");
				common.sendErrorsInRegistryResponse(er);
				return false;
			}
			
			SoapMessageValidator smv = (SoapMessageValidator) mv;
			OMElement query = smv.getMessageBody();
			
			boolean validateOk = validateHomeCommunityId(er, query, false);
			if (!validateOk)
				return false;

			XcQuerySim xcqSim = new XcQuerySim(common, asc);
			mvc.addMessageValidator("XcQuerySim", xcqSim, er);

			mvc.run();

			// Add in errors
			AdhocQueryResponseGenerator ahqrg = new AdhocQueryResponseGenerator(common, xcqSim);
			mvc.addMessageValidator("Attach Errors", ahqrg, er);
			mvc.run();
			sqs = ahqrg;

			// wrap in soap wrapper and http wrapper
			mvc.addMessageValidator("ResponseInSoapWrapper", new SoapWrapperRegistryResponseSim(common, sqs), er);

			// this will only run the new validators
			mvc.run();
			
			return true; // no updates anyway

		} 
		else if (transactionType.equals(TransactionType.IG_RETRIEVE.getShortName())) {
		}
			
		else {
			er.err(Code.XDSRegistryError, "Don't understand transaction " + transactionType, "InitiatingGatewayActorSimulator", "");
			common.sendFault("Don't understand transaction " + transactionType, null);
			return true;
		}
		



		return false;
	}


}
