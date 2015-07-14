package gov.nist.toolkit.simulators.sim.rep;

import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ATFactory.TransactionType;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.registrymsg.registry.RegistryErrorListGenerator;
import gov.nist.toolkit.registrymsg.registry.Response;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.simulators.sim.reg.RegistryResponseGeneratorSim;
import gov.nist.toolkit.simulators.sim.reg.SoapWrapperRegistryResponseSim;
import gov.nist.toolkit.simulators.support.DsActorSimulator;
import gov.nist.toolkit.simulators.support.DsSimCommon;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.valregmsg.message.SoapMessageValidator;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.errorrecording.GwtErrorRecorderBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.axiom.om.OMElement;

public class RepositoryActorSimulator extends DsActorSimulator {
	RepIndex repIndex;
	SimDb db;
	HttpServletResponse response;
	String repositoryUniqueId;
	SimulatorConfig asc;
	
	public RepositoryActorSimulator(RepIndex repIndex, SimCommon common, DsSimCommon dsSimCommon, SimDb db, SimulatorConfig asc, HttpServletResponse response, String repositoryUniqueId) {
		super(common, dsSimCommon);
		this.repIndex = repIndex;
		this.db = db;
		this.response = response;
		this.repositoryUniqueId = repositoryUniqueId;
		this.asc = asc;
	}
	 
	public boolean run(TransactionType transactionType, MessageValidatorEngine mvc, String validation) throws IOException {
		GwtErrorRecorderBuilder gerb = new GwtErrorRecorderBuilder();
		
		if (transactionType.equals(TransactionType.PROVIDE_AND_REGISTER)) {
			
			common.vc.isPnR = true;
			common.vc.xds_b = true;
			common.vc.isRequest = true;
			common.vc.hasHttp = true;
			common.vc.hasSoap = true;
			
			if (!dsSimCommon.runInitialValidationsAndFaultIfNecessary())
				return false;
			
			if (mvc.hasErrors()) {
				dsSimCommon.sendErrorsInRegistryResponse(er);
				return false;
			}
			
			RepPnRSim pnrSim = new RepPnRSim(common, dsSimCommon, asc);
			mvc.addMessageValidator("PnR", pnrSim, gerb.buildNewErrorRecorder());
			
			RegistryResponseGeneratorSim rrg = new RegistryResponseGeneratorSim(common, dsSimCommon);
			
			mvc.addMessageValidator("Attach Errors", rrg, gerb.buildNewErrorRecorder());
						
			// wrap in soap wrapper and http wrapper
			// auto-detects need for multipart/MTOM
			mvc.addMessageValidator("ResponseInSoapWrapper", new SoapWrapperRegistryResponseSim(common, dsSimCommon, rrg), gerb.buildNewErrorRecorder());
			
			mvc.run();
			
			return true;
			
		}
		else if (transactionType.equals(TransactionType.RETRIEVE)) {
						
			common.vc.isRet = true;
			common.vc.xds_b = true;
			common.vc.isRequest = true;
			common.vc.hasHttp = true;
			common.vc.hasSoap = true;
			
			if (!dsSimCommon.runInitialValidationsAndFaultIfNecessary())
				return false;
			
			if (mvc.hasErrors()) {
				dsSimCommon.sendErrorsInRegistryResponse(er);
				return false;
			}

			SoapMessageValidator smv = (SoapMessageValidator) common.getMessageValidatorIfAvailable(SoapMessageValidator.class);
			if (smv == null) {
				er.err(Code.XDSRepositoryError, "Internal Error: cannot find SoapMessageValidator.class", "RepositoryActorSimulator.java", null);
				return false;
			}
			OMElement retrieveRequest = smv.getMessageBody();
			
			List<String> docUids = new ArrayList<String>();
			for (OMElement uidEle : MetadataSupport.decendentsWithLocalName(retrieveRequest, "DocumentUniqueId")) {
				String uid = uidEle.getText();
				docUids.add(uid);
			}
			
			DocumentResponseSim dms = new DocumentResponseSim(common.vc, docUids, common, dsSimCommon, repositoryUniqueId);
			mvc.addMessageValidator("Generate DocumentResponse", dms, gerb.buildNewErrorRecorder());
			
			mvc.run();
			
			// generate special retrieve response message
			Response resp = dms.getResponse();
			// add in any errors collected
			try {
				RegistryErrorListGenerator relg = dsSimCommon.getRegistryErrorList();
				resp.add(relg, null);
			} catch (Exception e) {}

			// wrap in soap wrapper and http wrapper
			// auto-detects need for multipart/MTOM
			mvc.addMessageValidator("ResponseInSoapWrapper", new SoapWrapperRegistryResponseSim(common, dsSimCommon, dms), gerb.buildNewErrorRecorder());
			
			mvc.run();
			
			

			return true;
		}
		else {
			dsSimCommon.sendFault("Don't understand transaction " + transactionType, null);
			return false;
		} 


	}

}
