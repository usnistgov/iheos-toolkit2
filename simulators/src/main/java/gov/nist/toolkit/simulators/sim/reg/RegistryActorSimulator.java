package gov.nist.toolkit.simulators.sim.reg;

import gov.nist.toolkit.actorfactory.RegistryActorFactory;
import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ATFactory;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.simulators.sim.reg.mu.MuSim;
import gov.nist.toolkit.simulators.sim.reg.sq.SqSim;
import gov.nist.toolkit.simulators.sim.reg.store.Committer;
import gov.nist.toolkit.simulators.sim.reg.store.MetadataCollection;
import gov.nist.toolkit.simulators.support.ActorSimulator;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;

import java.io.IOException;

import org.apache.log4j.Logger;

public class RegistryActorSimulator extends ActorSimulator {
	SimDb db;
	SimulatorConfig asc;
	static Logger logger = Logger.getLogger(RegistryActorSimulator.class);
	boolean updateEnabled;

	public RegistryActorSimulator(SimCommon common, SimDb db, SimulatorConfig asc) {
		super(common);
		this.db = db;
		this.asc = asc;
		SimulatorConfigElement updateConfig = asc.get(RegistryActorFactory.update_metadata_option);
		updateEnabled = updateConfig.asBoolean();
	}


	public boolean run(ATFactory.TransactionType transactionType, MessageValidatorEngine mvc, String validation) throws IOException {
		AdhocQueryResponseGenerator queryResponseGenerator;
		RegistryResponseGeneratorSim registryResponseGenerator;		
		
		common.getValidationContext().updateEnabled = updateEnabled;
		
		if (transactionType.equals(ATFactory.TransactionType.REGISTER)) {

			common.vc.isR = true;
			common.vc.xds_b = true;
			common.vc.isRequest = true;
			common.vc.hasHttp = true;
			common.vc.hasSoap = true;

			if (!common.runInitialValidations())
				return false;  // returns if SOAP Fault was generated
			
			if (mvc.hasErrors()) {
				common.sendErrorsInRegistryResponse(er);
				return false;
			}

			
			RegRSim rsim = new RegRSim(common, asc);
			mvc.addMessageValidator("RegistryActor", rsim, er);

			registryResponseGenerator = new RegistryResponseGeneratorSim(common);
			mvc.addMessageValidator("Attach Errors", registryResponseGenerator, er);

			mvc.run();

			// wrap in soap wrapper and http wrapper
			mvc.addMessageValidator("ResponseInSoapWrapper", 
					new SoapWrapperRegistryResponseSim(common, registryResponseGenerator), 
					er);

			// catch up on validators to be run so we can judge whether to commit or not
			mvc.run();

			// commit updates (delta) to registry database
			if (!common.hasErrors())
				commit(mvc, common, rsim.delta);
			
			return !common.hasErrors();

		}
		else if (transactionType.equals(ATFactory.TransactionType.STORED_QUERY)) {

			common.vc.isSQ = true;
			common.vc.xds_b = true;
			common.vc.isRequest = true;
			common.vc.hasHttp = true;
			common.vc.hasSoap = true;

			if (!common.runInitialValidations())
				return false;
			
			if (mvc.hasErrors()) {
				common.sendErrorsInRegistryResponse(er);
				return false;
			}


			SqSim sqsim = new SqSim(common);
			mvc.addMessageValidator("SqSim", sqsim, er);

			mvc.run();

			// Add in errors
			queryResponseGenerator = new AdhocQueryResponseGenerator(common, sqsim);
			mvc.addMessageValidator("Attach Errors", queryResponseGenerator, er);
			mvc.run();

			// wrap in soap wrapper and http wrapper
			mvc.addMessageValidator("ResponseInSoapWrapper", new SoapWrapperRegistryResponseSim(common, queryResponseGenerator), er);

			// this will only run the new validators
			mvc.run();
			
			return true; // no updates anyway

		}
		else if (transactionType.equals(ATFactory.TransactionType.UPDATE)) {

			common.vc.isMU = true;
			common.vc.isRequest = true;

			if (!common.runInitialValidations())
				return false;

			if (mvc.hasErrors()) {
				common.sendErrorsInRegistryResponse(er);
				return false;
			}

			MuSim musim = new MuSim(common, asc);
			mvc.addMessageValidator("MuSim", musim, er);
			
			mvc.run();

			MetadataPatternValidator mpv = new MetadataPatternValidator(common, validation);
			mvc.addMessageValidator("MetadataPatternValidator", mpv, er);
			
			mvc.run();
			

			registryResponseGenerator = new RegistryResponseGeneratorSim(common);
			mvc.addMessageValidator("Attach Errors", registryResponseGenerator, er);

			mvc.run();

			// wrap in soap wrapper and http wrapper
			mvc.addMessageValidator("ResponseInSoapWrapper", new SoapWrapperRegistryResponseSim(common, registryResponseGenerator), er);

			// run all the queued up validators so we can check for errors
			mvc.run();

			if (!common.hasErrors())
				commit(mvc, common, musim.delta);


			return !common.hasErrors();

		}
		else {
			common.sendFault("Don't understand transaction " + transactionType, null);
			return false;
		}


	}

	void commit(MessageValidatorEngine mvc,
			SimCommon common, MetadataCollection delta)
	throws IOException {

		logger.info("Committing:");
		// changes to be committed
		logger.info(delta.subSetCollection.toString());
		logger.info(delta.docEntryCollection.toString());
		logger.info(delta.folCollection.toString());
		logger.info(delta.updatedFolCollection.toString() + " updated");
		logger.info(delta.assocCollection.toString());

		delta.mkDirty();

		synchronized(common.regIndex) {
			Committer com = new Committer(common, delta);

			mvc.addMessageValidator("Commit", com, er);

			mvc.run();

			if (!common.hasErrors()) {
				common.regIndex.save();
			}
		}
	}
}
