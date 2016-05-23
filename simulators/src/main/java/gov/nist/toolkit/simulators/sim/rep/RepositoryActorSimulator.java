package gov.nist.toolkit.simulators.sim.rep;

import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.actorfactory.client.NoSimException;
import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actorfactory.client.SimulatorStats;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.errorrecording.GwtErrorRecorderBuilder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.registrysupport.RegistryErrorListGenerator;
import gov.nist.toolkit.registrymsg.registry.Response;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.simulators.servlet.SimServlet;
import gov.nist.toolkit.simulators.sim.reg.RegistryResponseGeneratorSim;
import gov.nist.toolkit.simulators.sim.reg.SoapWrapperRegistryResponseSim;
import gov.nist.toolkit.simulators.support.BaseDsActorSimulator;
import gov.nist.toolkit.simulators.support.DsSimCommon;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.validatorsSoapMessage.message.SoapMessageValidator;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RepositoryActorSimulator extends BaseDsActorSimulator {
	static final Logger logger = Logger.getLogger(RepositoryActorSimulator.class);

	RepIndex repIndex;
//	SimDb db;
	String repositoryUniqueId;
	private boolean forward = true;

	static List<TransactionType> transactions = new ArrayList<>();

	static {
		transactions.add(TransactionType.PROVIDE_AND_REGISTER);
		transactions.add(TransactionType.RETRIEVE);
	}

	public boolean supports(TransactionType transactionType) {
		return transactions.contains(transactionType);
	}

	public RepositoryActorSimulator(RepIndex repIndex, SimCommon common, DsSimCommon dsSimCommon, SimDb db, SimulatorConfig simulatorConfig, HttpServletResponse response, String repositoryUniqueId) {
		super(common, dsSimCommon);
		this.repIndex = repIndex;
		this.db = db;
		this.response = response;
		this.repositoryUniqueId = repositoryUniqueId;
		setSimulatorConfig(simulatorConfig);
	}

	public RepositoryActorSimulator(DsSimCommon dsSimCommon, SimulatorConfig simulatorConfig) {
		super(dsSimCommon.simCommon, dsSimCommon);
		this.repIndex = dsSimCommon.repIndex;
		this.db = dsSimCommon.simCommon.db;;
		this.response = dsSimCommon.simCommon.response;
        setSimulatorConfig(simulatorConfig);
		init();
	}

	public RepositoryActorSimulator() {}

	public void init() {
		SimulatorConfigElement configEle = getSimulatorConfig().get("repositoryUniqueId");
		if (configEle != null)   // happens when used to implement a Document Recipient
			this.repositoryUniqueId = configEle.asString();
	}

    @Override
	public boolean run(TransactionType transactionType, MessageValidatorEngine mvc, String validation) throws IOException {
		GwtErrorRecorderBuilder gerb = new GwtErrorRecorderBuilder();

		logger.debug("Repository starting transaction " + transactionType);

		if (transactionType.equals(TransactionType.PROVIDE_AND_REGISTER) ||
				transactionType.equals(TransactionType.XDR_PROVIDE_AND_REGISTER)) {

			common.vc.isPnR = true;
			common.vc.xds_b = true;
			common.vc.isXDR = false;
			common.vc.isRequest = true;
			common.vc.hasHttp = true;
			common.vc.hasSoap = true;

			if (transactionType.equals(TransactionType.XDR_PROVIDE_AND_REGISTER)) {
				logger.info("XDR style of PnR");
				common.vc.isXDR = true;
			}

			if (!dsSimCommon.runInitialValidationsAndFaultIfNecessary())
				return false;

			if (mvc.hasErrors()) {
				dsSimCommon.sendErrorsInRegistryResponse(er);
				return false;
			}

			RepPnRSim pnrSim = new RepPnRSim(common, dsSimCommon, getSimulatorConfig());
			pnrSim.setForward(forward);
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
			for (OMElement uidEle : XmlUtil.decendentsWithLocalName(retrieveRequest, "DocumentUniqueId")) {
				String uid = uidEle.getText();
				docUids.add(uid);
			}

			RetrieveDocumentResponseSim dms = new RetrieveDocumentResponseSim(common.vc, docUids, common, dsSimCommon, repositoryUniqueId);
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
			dsSimCommon.sendFault("RepositoryActorSimulator: Don't understand transaction " + transactionType, null);
			return false;
		}
	}

	static public SimulatorStats getSimulatorStats(SimId simId) throws IOException, NoSimException {
		RepIndex repIndex = SimServlet.getRepIndex(simId);
		return repIndex.getSimulatorStats();
	}

	public boolean isForward() {
		return forward;
	}

	public void setForward(boolean forward) {
		this.forward = forward;
	}
}
