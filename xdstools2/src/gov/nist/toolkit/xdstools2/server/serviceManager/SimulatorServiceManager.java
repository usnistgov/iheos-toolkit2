package gov.nist.toolkit.xdstools2.server.serviceManager;

import gov.nist.toolkit.actorfactory.CommonServiceManager;
import gov.nist.toolkit.actorfactory.SimManager;
import gov.nist.toolkit.actorfactory.SimulatorFactory;
import gov.nist.toolkit.actorfactory.SiteServiceManager;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ATFactory.ActorType;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.http.HttpHeader.HttpHeaderParseException;
import gov.nist.toolkit.http.HttpParseException;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.results.ResultBuilder;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.simDb.SimDb;
import gov.nist.toolkit.simulators.support.SimInstanceTerminator;
import gov.nist.toolkit.simulators.support.ValidateMessageService;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.valsupport.client.MessageValidationResults;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.xdsexception.EnvironmentNotSelectedException;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdstools2.server.simulator.support.ServletSimulator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Each new request should go to a new instance.  All persistence
 * between calls is done by storing on disk or in the session
 * object.
 * @author bill
 *
 */
public class SimulatorServiceManager extends CommonServiceManager {
	static Logger logger = Logger.getLogger(SimulatorServiceManager.class);
	
	Session session;

	public SimulatorServiceManager(Session session)  {
		this.session = session;
	}
	
	public List<String> getTransInstances(String simid, String xactor, String trans) throws Exception
	{
		logger.debug(session.id() + ": " + "getTransInstances : " + simid + " - " + xactor + " - " + trans);
		SimDb simdb;
		try {
			simdb = SimManager.get(session.id()).getSimDb(simid);
		} catch (IOException e) {
			logger.error("getTransInstances", e);
			throw new Exception(e.getMessage());
		}
		ActorType actor = simdb.getSimulatorActorType();
		return simdb.getTransInstances(actor.toString(), trans);
	}

	public List<Result> getSelectedMessage(String simFileSpec) {
		logger.debug(session.id() + ": " + "getSelectedMessage");
		List<Result> results = new ArrayList<Result>();
		Result result = ResultBuilder.RESULT("getSelectedMessage");
		results.add(result);
		try {
			SimDb sdb = SimManager.get(session.id()).getSimDb(session.getDefaultSimId());
			String httpMsgHdr = sdb.getRequestMessageHeader(simFileSpec);
			byte[] httpMsgBody = sdb.getRequestMessageBody(simFileSpec);
			result.setText(httpMsgHdr + new String(httpMsgBody));
		} catch (IOException e) {
			result.assertions.add(ExceptionUtil.exception_details(e), false);
		}
		return results;
	}

	public List<Result> getSelectedMessageResponse(String simFileSpec) {
		logger.debug(session.id() + ": " + "getSelectedMessageResponse");
		List<Result> results = new ArrayList<Result>();
		Result result = ResultBuilder.RESULT("getSelectedMessageResponse");
		results.add(result);
		try {
			SimDb sdb = SimManager.get(session.id()).getSimDb(session.getDefaultSimId());
			String httpMsgHdr = sdb.getResponseMessageHeader(simFileSpec);
			byte[] httpMsgBody = sdb.getResponseMessageBody(simFileSpec);
			result.setText(httpMsgHdr + new String(httpMsgBody));
		} catch (IOException e) {
			result.assertions.add(ExceptionUtil.exception_details(e), false);
		}
		return results;
	}

	public String getSimulatorEndpoint() {
		logger.debug(session.id() + ": " + "getSimulatorEndpoint");
		return session.getSimBaseEndpoint();
	}

	public void deleteSimFile(String simFileSpec) throws Exception  {
		logger.debug(session.id() + ": " + "deleteSimFile");
		try {
			SimDb sdb = SimManager.get(session.id()).getSimDb(session.getDefaultSimId());
			sdb.delete(simFileSpec);
		} catch (IOException e) {
			logger.error("deleteSimFile", e);
			throw new Exception(e.getMessage());
		}
	}

	public void renameSimFile(String simFileSpec, String newSimFileSpec)
			throws Exception {
		logger.debug(session.id() + ": " + "renameSimFile");
		SimDb sdb = SimManager.get(session.id()).getSimDb(session.getDefaultSimId());
		sdb.rename(simFileSpec, newSimFileSpec);
	}

	public MessageValidationResults executeSimMessage(String simFileSpec) {
		logger.debug(session.id() + ": " + "executeSimMessage");
		try {
			SimDb db = SimManager.get(session.id()).getSimDb(session.getDefaultSimId());
			db.setFileNameBase(simFileSpec);
			ServletSimulator ss = new ServletSimulator(db);
			ss.post();
			MessageValidationResults mvr = ss.getMessageValidationResults();
			return mvr;
		} catch (IOException e) {
			MessageValidationResults mvr = new MessageValidationResults();
			mvr.addError(XdsErrorCode.Code.NoCode, "Exception",
					ExceptionUtil.exception_details(e));
			return mvr;
		} catch (HttpParseException e) {
			MessageValidationResults mvr = new MessageValidationResults();
			mvr.addError(XdsErrorCode.Code.NoCode, "Exception",
					ExceptionUtil.exception_details(e));
			return mvr;
		} catch (HttpHeaderParseException e) {
			MessageValidationResults mvr = new MessageValidationResults();
			mvr.addError(XdsErrorCode.Code.NoCode, "Exception",
					ExceptionUtil.exception_details(e));
			return mvr;
		}
	}

	public List<String> getTransactionsForSimulator(String simid) throws Exception  {
		logger.debug(session.id() + ": " + "getTransactionsForSimulator(" + simid + ")");
		SimDb simdb;
		try {
			simdb = SimManager.get(session.id()).getSimDb(simid);
		} catch (IOException e) {
			logger.error("getTransactionsForSimulator", e);
			throw new Exception(e.getMessage(),e);
		}
		return simdb.getTransactionsForSimulator();
	}

	public String getTransactionRequest(String simid, String actor,
			String trans, String event) {
		logger.debug(session.id() + ": " + "getTransactionRequest - " + simid + " - " + actor + " - " + trans + " - " + event);
		try {
			SimDb db = SimManager.get(session.id()).getSimDb(simid);

			if (actor == null)
				actor = db.getSimulatorActorType().toString();

			File headerFile = db.getRequestHeaderFile(simid, actor, trans,
					event);
			File bodyFile = db.getRequestBodyFile(simid, actor, trans, event);

			return Io.stringFromFile(headerFile)
					// + "\r\n"
					+ new String(Io.bytesFromFile(bodyFile));
		} catch (Exception e) {
			return "Data not available";
		}
	}

	public String getTransactionResponse(String simid, String actor,
			String trans, String event) {
		logger.debug(session.id() + ": " + "getTransactionResponse - " + simid + " - " + actor + " - " + trans + " - " + event);
		try {
			SimDb db = SimManager.get(session.id()).getSimDb(simid);

			if (actor == null)
				actor = db.getSimulatorActorType().toString();

			File headerFile = db.getResponseHeaderFile(simid, actor, trans,
					event);
			File bodyFile = db
					.getResponseBodyFile(simid, actor, trans, event);

			return Io.stringFromFile(headerFile)
					// + "\r\n"
					+ Io.stringFromFile(bodyFile);
		} catch (Exception e) {
			return "Data not available";
		}
	}

	public String getTransactionLog(String simid, String actor, String trans,
			String event) {
		logger.debug(session.id() + ": " + "getTransactionLog - " + simid + " - " + actor + " - " + trans + " - " + event);
		try {
			SimDb db = SimManager.get(session.id()).getSimDb(simid);

			if (actor == null)
				actor = db.getSimulatorActorType().toString();

			File logFile = db.getLogFile(simid, actor, trans, event);

			return Io.stringFromFile(logFile);
		} catch (Exception e) {
			return "Data not available";
		}
	}

	public List<SimulatorConfig> getNewSimulator(String actorTypeName) throws Exception  {
		logger.debug(session.id() + ": " + "getNewSimulator");
		try {
			List<SimulatorConfig> scl = new SimulatorFactory(SimManager.get(session.id())).buildNewSimulator(SimManager.get(session.id()), actorTypeName);
			SimManager.get(session.id()).addSimConfigs(scl);
			SimulatorConfig sc = scl.get(0);
			logger.info("New simulator for session " + session.id() + ": " + actorTypeName + " ==> " + sc.getId());
			return scl;
		} catch (EnvironmentNotSelectedException e) {
			logger.error("Environment Not Selected");
			throw new Exception("Environment Not Selected");
		} catch (Exception e) {
			logger.error("getNewSimulator:\n" + ExceptionUtil.exception_details(e));
			throw new Exception(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	public List<SimulatorConfig> getSimConfigs(List<String> ids) throws Exception  {
		logger.debug(session.id() + ": " + "getSimConfigs " + ids);
		
		// Carefully now, some simulators may have expired, return only those that still exist
		List<SimulatorConfig> configs = new ArrayList<SimulatorConfig>();
		List<String> tmpIdList = new ArrayList<String>();
		List<String> goodIdList = new ArrayList<String>();
		SimulatorFactory simFact = new SimulatorFactory(SimManager.get(session.id()));
		for (String id : ids) {
			tmpIdList.clear();
			tmpIdList.add(id);
			List<SimulatorConfig> configList = simFact.loadSimulators(tmpIdList);
			if (!configList.isEmpty() && !configList.get(0).isExpired()) {
				goodIdList.add(id);
				configs.add(configList.get(0));
			}
		}
		
		try {
//			List<SimulatorConfig> configs = new SimulatorFactory(SimManager.get(session.id())).loadSimulators(ids);

			SiteServiceManager.getSiteServiceManager().loadSites(session.id());

			session.loadActorSimulatorConfigs(SiteServiceManager.getSiteServiceManager().getSites(), goodIdList);

			return configs;
		} catch (Throwable e) {
			logger.error("getSimConfigs", e);
			throw new Exception(e.getMessage());
		}
	}

	public String putSimConfig(SimulatorConfig config) throws Exception  {
		logger.debug(session.id() + ": " + "putSimConfig");
		try {
			new SimulatorFactory(SimManager.get(session.id())).saveConfiguration(config);
		} catch (IOException e) {
			logger.error("putSimConfig", e);
			throw new Exception(e.getMessage());
		}
		return "";
	}

	public String deleteConfig(SimulatorConfig config) throws Exception  {
		logger.debug(session.id() + ": " + "deleteConfig " + config.getId());
		try {
			session.deleteActorSimulator(config);
		} catch (IOException e) {
			logger.error("deleteConfig", e);
			throw new Exception(e.getMessage());
		}
		return "";
	}

	/**
	 * 
	 * @return map from simulator name (private name) to simulator id (global id)
	 */

	public Map<String, String> getActorSimulatorNameMap() {
		logger.debug(session.id() + ": " + "getActorSimulatorNameMap");
		return session.getActorSimulatorNameMap();
	}

	public int removeOldSimulators() {
		logger.debug(session.id() + ": " + "removeOldSimulators");
		try {
			return new SimInstanceTerminator(session).run();
		} catch (Exception e) {
			logger.error("removeOldSimulators failed", e);
			return 0;
		}
	}

	public File getSimDbFile() {
		return Installation.installation().propertyServiceManager().getSimDbDir();
	}

	public MessageValidationResults validateMessage(ValidationContext vc) {
		logger.debug(session.id() + ": " + "validateMessage");
		try {
			vc.setCodesFilename(session.getCodesFile().toString());
		} catch (Exception e) {}

		try {

			ValidateMessageService vm = new ValidateMessageService(session, null);
			MessageValidationResults mvr = vm.validateLastUpload(vc);
			return mvr;
		} 
		catch (RuntimeException e) {
			MessageValidationResults mvr = new MessageValidationResults();
			mvr.addError(XdsErrorCode.Code.NoCode, "Exception",
					ExceptionUtil.exception_details(e));
			return mvr;
		}
	}

	public MessageValidationResults validateMessage(ValidationContext vc,
			String simFileName) {
		logger.debug(session.id() + ": " + "validateMessage");
		try {
			vc.setCodesFilename(session.getCodesFile().toString());
		} catch (Exception e) {}
		try {
			ValidateMessageService vm = new ValidateMessageService(session, null);
			MessageValidationResults mvr = vm.validateMessageFile(vc,
					simFileName);
			return mvr;
		} 
		catch (RuntimeException e) {
			MessageValidationResults mvr = new MessageValidationResults();
			mvr.addError(XdsErrorCode.Code.NoCode, "Exception",
					ExceptionUtil.exception_details(e));
			return mvr;
		}
	}

}
