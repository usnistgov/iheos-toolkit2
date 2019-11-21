package gov.nist.toolkit.fhir.simulators.sim.rep;

import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties;
import gov.nist.toolkit.errorrecording.GwtErrorRecorderBuilder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.registrymsg.registry.Response;
import gov.nist.toolkit.registrysupport.RegistryErrorListGenerator;
import gov.nist.toolkit.simcommon.client.NoSimException;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.client.SimulatorStats;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.simcommon.server.SimDb;
import gov.nist.toolkit.fhir.simulators.servlet.SimServlet;
import gov.nist.toolkit.fhir.simulators.sim.reg.RegistryResponseGeneratorSim;
import gov.nist.toolkit.fhir.simulators.sim.reg.SoapWrapperRegistryResponseSim;
import gov.nist.toolkit.fhir.simulators.support.BaseDsActorSimulator;
import gov.nist.toolkit.fhir.simulators.support.DsSimCommon;
import gov.nist.toolkit.simcommon.server.SimCommon;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.validatorsSoapMessage.message.SoapMessageValidator;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RepositoryActorSimulator extends BaseDsActorSimulator {
	static final Logger logger = Logger.getLogger(RepositoryActorSimulator.class);

	RepIndex repIndex;
//	SimDb db;
	String repositoryUniqueId;
	private boolean forward = true;
	private boolean rd_enabled = false;

	static List<TransactionType> transactions = new ArrayList<>();

	static {
		transactions.add(TransactionType.XDR_PROVIDE_AND_REGISTER);
		transactions.add(TransactionType.PROVIDE_AND_REGISTER);
		transactions.add(TransactionType.RETRIEVE);
		transactions.add(TransactionType.REMOVE_DOCUMENTS);
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
		configEle = getSimulatorConfig().get(SimulatorProperties.REMOVE_DOCUMENTS);
		rd_enabled = (configEle != null) ?  configEle.asBoolean() : false;
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

			SimulatorConfigElement sce = dsSimCommon.getSimulatorConfig().getConfigEle(SimulatorProperties.METADATA_LIMITED);
			if (sce.asBoolean()) {
				common.vc.isXDRLimited = true;
			}

			if (transactionType.equals(TransactionType.XDR_PROVIDE_AND_REGISTER)) {
				logger.info("XDR style of PnR");
				common.vc.isXDR = true;
			}

			if (!dsSimCommon.verifySubmissionAllowed())
				return false;

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

			SoapMessageValidator smv = (SoapMessageValidator) dsSimCommon.getMessageValidatorIfAvailable(SoapMessageValidator.class);
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
		else if (transactionType.equals(TransactionType.REMOVE_DOCUMENTS)) {
			if (!rd_enabled) {
				dsSimCommon.sendFault("RMD not enabled on this actor ", null);
				return false;
			}

			common.vc.isRD = true;
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

			SoapMessageValidator smv = (SoapMessageValidator) dsSimCommon.getMessageValidatorIfAvailable(SoapMessageValidator.class);
			if (smv == null) {
				er.err(Code.XDSRepositoryError, "Internal Error: cannot find SoapMessageValidator.class", "RepositoryActorSimulator.java", null);
				return false;
			}
			OMElement removeRequest = smv.getMessageBody();

			List<RepIdUidPair> pairs = new ArrayList<>();
			for (OMElement requestEle : XmlUtil.decendentsWithLocalName(removeRequest, "DocumentRequest")) {
				String uid = null;
				String repUid = null;
				Iterator it = requestEle.getChildElements();
				while ( it.hasNext()) {
					OMElement ele = (OMElement) it.next();
					if ("RepositoryUniqueId".equals(ele.getLocalName()))
						repUid = ele.getText();
					else if ("DocumentUniqueId".equals(ele.getLocalName()))
						uid = ele.getText();
				}
				RepIdUidPair pair = new RepIdUidPair(repUid, uid);
				pairs.add(pair);
			}

			RemoveDocumentResponseSim dms = new RemoveDocumentResponseSim(common.vc, pairs, common, dsSimCommon, repositoryUniqueId);
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
