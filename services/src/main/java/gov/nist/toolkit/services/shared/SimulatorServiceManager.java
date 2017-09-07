package gov.nist.toolkit.services.shared;

import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.actortransaction.client.TransactionInstance;
import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.errorrecording.GwtErrorRecorderBuilder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.http.HttpHeader;
import gov.nist.toolkit.http.HttpParseException;
import gov.nist.toolkit.http.ParseException;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymetadata.MetadataParser;
import gov.nist.toolkit.results.CommonService;
import gov.nist.toolkit.results.ResultBuilder;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.services.client.EnvironmentNotSelectedClientException;
import gov.nist.toolkit.services.server.SimulatorApi;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.simcommon.client.*;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.simcommon.server.GenericSimulatorFactory;
import gov.nist.toolkit.simcommon.server.SimCache;
import gov.nist.toolkit.simcommon.server.SimDb;
import gov.nist.toolkit.simcommon.server.SimManager;
import gov.nist.toolkit.simulators.servlet.ServletSimulator;
import gov.nist.toolkit.simulators.servlet.SimServlet;
import gov.nist.toolkit.simulators.sim.reg.RegistryActorSimulator;
import gov.nist.toolkit.simulators.sim.rep.RepositoryActorSimulator;
import gov.nist.toolkit.simulators.sim.rep.od.OddsActorSimulator;
import gov.nist.toolkit.simulators.support.SimInstanceTerminator;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.utilities.message.MultipartFormatter;
import gov.nist.toolkit.validatorsSoapMessage.engine.ValidateMessageService;
import gov.nist.toolkit.valsupport.client.MessageValidationResults;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.client.EnvironmentNotSelectedException;
import groovy.json.JsonOutput;
import groovy.util.XmlNodePrinter;
import groovy.util.XmlParser;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;


import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;


/**
 * Each new request should go to a new instance.  All persistence
 * between calls is done by storing on disk or in the session
 * model.
 * @author bill
 *
 */
public class SimulatorServiceManager extends CommonService {
	static Logger logger = Logger.getLogger(SimulatorServiceManager.class);

	Session session;

	public SimulatorServiceManager(Session session)  {
		this.session = session;
	}

	public List<TransactionInstance> getTransInstances(SimId simid, String xactor, String trans) throws Exception
	{
		logger.debug(session.id() + ": " + "getTransInstances : " + simid + " - " + xactor + " - " + trans);
		return GenericSimulatorFactory.getTransInstances(simid, xactor, trans);
	}

	public List<Result> getSelectedMessage(String simFileSpec) {
		logger.debug(session.id() + ": " + "getSelectedMessage");
		List<Result> results = new ArrayList<Result>();
		Result result = ResultBuilder.RESULT(new TestInstance("getSelectedMessage"));
		results.add(result);
		try {
			SimDb sdb = new SimDb(session.getDefaultSimId());
			String httpMsgHdr = sdb.getRequestMessageHeader(simFileSpec);
			byte[] httpMsgBody = sdb.getRequestMessageBody(simFileSpec);
			result.setText(httpMsgHdr + new String(httpMsgBody));
		} 
		catch (IOException e) {
			result.assertions.add(ExceptionUtil.exception_details(e), false);
		}
		catch (NoSimException e) {
			result.assertions.add(ExceptionUtil.exception_details(e), false);
		}
		return results;
	}

	public List<Result> getSelectedMessageResponse(String simFileSpec) {
		logger.debug(session.id() + ": " + "getSelectedMessageResponse");
		List<Result> results = new ArrayList<Result>();
		Result result = ResultBuilder.RESULT(new TestInstance("getSelectedMessageResponse"));
		results.add(result);
		try {
			SimDb sdb = new SimDb(session.getDefaultSimId());
			String httpMsgHdr = sdb.getResponseMessageHeader(simFileSpec);
			byte[] httpMsgBody = sdb.getResponseMessageBody(simFileSpec);
			result.setText(httpMsgHdr + new String(httpMsgBody));
		} 
		catch (IOException e) {
			result.assertions.add(ExceptionUtil.exception_details(e), false);
		}
		catch (NoSimException e) {
			result.assertions.add(ExceptionUtil.exception_details(e), false);
		}
		return results;
	}

	public String getSimulatorEndpoint() {
		logger.debug(session.id() + ": " + "getSimulatorEndpoint");
		return session.getSimBaseEndpoint();
	}

	public void deleteSimFile(String simFileSpec) throws Exception  {
		throw new Exception("Not Implemented");
	}

	public void renameSimFile(String simFileSpec, String newSimFileSpec)
			throws Exception {
		logger.debug(session.id() + ": " + "renameSimFile");
		GenericSimulatorFactory.renameSimFile(simFileSpec, newSimFileSpec);
	}

	public MessageValidationResults executeSimMessage(String simFileSpec) {
		logger.debug(session.id() + ": " + "executeSimMessage");
		try {
			SimDb db = new SimDb(session.getDefaultSimId());
			db.setFileNameBase(simFileSpec);
			ServletSimulator ss = new ServletSimulator(db);
			ss.post();
			MessageValidationResults mvr = ss.getMessageValidationResults();
			return mvr;
		} 
		catch (IOException e) {
			MessageValidationResults mvr = new MessageValidationResults();
			mvr.addError(XdsErrorCode.Code.NoCode, "Exception",
					ExceptionUtil.exception_details(e));
			return mvr;
		} 
		catch (HttpParseException e) {
			MessageValidationResults mvr = new MessageValidationResults();
			mvr.addError(XdsErrorCode.Code.NoCode, "Exception",
					ExceptionUtil.exception_details(e));
			return mvr;
		} 
		catch (HttpHeader.HttpHeaderParseException e) {
			MessageValidationResults mvr = new MessageValidationResults();
			mvr.addError(XdsErrorCode.Code.NoCode, "Exception",
					ExceptionUtil.exception_details(e));
			return mvr;
		}
		catch (NoSimException e) {
			MessageValidationResults mvr = new MessageValidationResults();
			mvr.addError(XdsErrorCode.Code.NoCode, "Exception",
					ExceptionUtil.exception_details(e));
			return mvr;
		} catch (ParseException e) {
			MessageValidationResults mvr = new MessageValidationResults();
			mvr.addError(XdsErrorCode.Code.NoCode, "Exception",
					ExceptionUtil.exception_details(e));
			return mvr;
		}
	}

	public List<String> getTransactionsForSimulator(SimId simid) throws Exception  {
		logger.debug(session.id() + ": " + "getTransactionsForSimulator(" + simid + ")");
		SimDb simdb = new SimDb(simid);
		return simdb.getTransactionsForSimulator();
	}

	public Message getTransactionRequest(SimId simid, String actor,
			String trans, String event) {
		logger.debug(session.id() + ": " + "getTransactionRequest - " + simid + " - " + actor + " - " + trans + " - " + event);
		try {
			SimDb db = new SimDb(simid);

			if (actor == null)
				actor = db.getSimulatorActorType().toString();

			File headerFile = db.getRequestHeaderFile(simid, actor, trans,
					event);
			File bodyFile = db.getRequestBodyFile(simid, actor, trans, event);

			String body = new String(Io.bytesFromFile(bodyFile));
			body = formatMessage(body);
			return new Message(Io.stringFromFile(headerFile), body);
		} catch (Exception e) {
			return new Message("Error: " + e.getMessage());
		}
	}

	private String formatMessage(String message) throws IOException, SAXException, ParserConfigurationException {
		String trimBody = message.trim();
		boolean isJson = trimBody.startsWith("{");
		boolean isXml = trimBody.startsWith("<");
		boolean isMultipart = trimBody.startsWith("--");
		if (isJson) {
			// format json but leave embedded HTML alone
			message = JsonOutput.prettyPrint(message);
		} else if (isXml) {
			message = formatXml(message);
		} else if (isMultipart) {
			message = MultipartFormatter.format(message);
		}
		return message;
	}

	private String formatXml(String xml) throws ParserConfigurationException, SAXException, IOException {
		StringWriter xmlOutput = new StringWriter();
		XmlNodePrinter printer = new XmlNodePrinter(new PrintWriter(xmlOutput));
		printer.print(new XmlParser().parseText(xml));
		xml = xmlOutput.toString();
		return xml;
	}

	public Message getTransactionResponse(SimId simid, String actor,
			String trans, String event) {
		logger.debug(session.id() + ": " + "getTransactionResponse - " + simid + " - " + actor + " - " + trans + " - " + event);
		try {
			SimDb db = new SimDb(simid);

			if (actor == null)
				actor = db.getSimulatorActorType().toString();

			File headerFile = db.getResponseHeaderFile(simid, actor, trans,
					event);
			File bodyFile = db
					.getResponseBodyFile(simid, actor, trans, event);

			String body = new String(Io.bytesFromFile(bodyFile));
			body = formatMessage(body);
			return new Message(
					((headerFile.exists()) ? Io.stringFromFile(headerFile) : "")
							, body);
		} catch (Exception e) {
			return new Message("Error: " + e.getMessage());
		}
	}

	public String getTransactionLog(SimId simid, String actor, String trans,
			String event) {
		logger.debug(session.id() + ": " + "getTransactionLog - " + simid + " - " + actor + " - " + trans + " - " + event);
		try {
			SimDb db = new SimDb(simid);

			if (actor == null)
				actor = db.getSimulatorActorType().toString();

			File logFile = db.getLogFile(simid, actor, trans, event);

			return Io.stringFromFile(logFile);
		} catch (Exception e) {
			return "Data not available";
		}
	}

	public Simulator getNewSimulator(String actorTypeName, SimId simID) throws Exception  {
		logger.debug(session.id() + ": " + "getNewSimulator(type=" + actorTypeName + ")");
		return new SimulatorApi(session).create(actorTypeName, simID);
	}


	/**
	 * Return SimualtorConfigs that are not expired. Configs may not have been
	 * created in this session so make sure they are present in SimCache
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<SimulatorConfig> getSimConfigs(List<SimId> ids) throws Exception  {
		logger.debug(session.id() + ": " + "getSimConfigs " + ids);

		GenericSimulatorFactory simFact = new GenericSimulatorFactory(SimCache.getSimManagerForSession(session.id()));
		List<SimulatorConfig> configs = simFact.loadAvailableSimulators(ids);
//		List<SimulatorConfig> configs = simServices.getSimConfigs(ids);

		return configs;
	}

	public List<SimulatorConfig> getAllSimConfigs(String user) throws Exception {
		logger.debug(session.id() + ": " + "getAllSimConfigs for " + user);
		if (user == null) return new ArrayList<>();

		GenericSimulatorFactory simFact = new GenericSimulatorFactory(SimCache.getSimManagerForSession(session.id()));

		List<SimId> simIds = SimDb.getAllSimIds();

		List<SimId> userSimIds = new ArrayList<>();
		for (SimId simId : simIds) {
			if (simId.isUser(user))
				userSimIds.add(simId);
		}

		List<SimulatorConfig> configs = GenericSimulatorFactory.loadSimulators(userSimIds);

		return configs;
	}

	public void updateAllSimulatorsHostAndPort(String host, String port) throws Exception, IOException, ClassNotFoundException {
		GenericSimulatorFactory simFact = new GenericSimulatorFactory(SimCache.getSimManagerForSession(session.id()));

		List<SimId> simIds = new SimDb().getAllSimIds();

		List<SimulatorConfig> configs = GenericSimulatorFactory.loadSimulators(simIds);
		for (SimulatorConfig config : configs) {
			List<SimulatorConfigElement> endpointElements = config.getEndpointConfigs();
			for (SimulatorConfigElement endpointElement : endpointElements) {

			}
		}
	}

	public String saveSimConfig(SimulatorConfig config) throws Exception  {
		logger.debug(session.id() + ": " + "saveSimConfig");
		try {
			SimManager simManager = SimCache.getSimManagerForSession(session.id(), true);
			new GenericSimulatorFactory(simManager).saveConfiguration(config);
		} catch (IOException e) {
			logger.error("saveSimConfig", e);
			throw e;
		}
        logger.debug("save complete");
		return "";
	}

    public String deleteConfig(SimId simId) throws Exception {
        SimulatorConfig config = new SimDb().getSimulator(simId);
        if (config != null)
            return deleteConfig(config);
        if (SimDb.exists(simId)) {
            try {
                new SimDb(simId).delete();
            } catch (Exception e) {
                // ignore
            }
        }
        return "";
    }

	public String deleteConfig(SimulatorConfig config) throws Exception  {
		logger.debug(session.id() + ": " + "deleteConfig " + config.getId());
        GenericSimulatorFactory.delete(config.getId());
        new SimulatorApi(session).delete(config.getId());
		SimServlet.deleteSim(config.getId());
		return "";
//		try {
//			new SimCache().deleteSimConfig(config.getId());
//		} catch (IOException e) {
//			logger.error("deleteConfig", e);
//			throw new Exception(e.getMessage());
//		}
//		return "";
	}

	/**
	 * get all SimIds
	 * @param userFilter - if not null, only return simids for this user
	 * @return
	 */
	public List<SimId> getSimIds(String userFilter) {
		logger.debug(session.id() + ": " + "getSimIds for " + userFilter);
		List<SimId> all = SimDb.getAllSimIds();
		if (userFilter == null) return all;
		List<SimId> filtered = new ArrayList<>();
		for (SimId sid : all) {
			if (userFilter.equals(sid.getUser()))
				filtered.add(sid);
		}
		return filtered;
	}

	public int removeOldSimulators() {
		logger.debug(session.id() + ": " + "removeOldSimulators");
		try {
			return new SimInstanceTerminator().run();
		} catch (Exception e) {
			logger.error("removeOldSimulators failed", e);
			return 0;
		}
	}

	public MessageValidationResults validateMessage(ValidationContext vc) throws EnvironmentNotSelectedClientException {
		logger.debug(session.id() + ": " + "validateMessage");
		try {
			File codesFile = session.getCodesFile();
			vc.setCodesFilename(codesFile.toString());
		} catch (EnvironmentNotSelectedException e) {
			throw new EnvironmentNotSelectedClientException(e.getMessage());
		}
		try {
			MessageValidationResults mvr = validateLastUpload(vc);
			return mvr;
		} 
		catch (RuntimeException e) {
			MessageValidationResults mvr = new MessageValidationResults();
			mvr.addError(XdsErrorCode.Code.NoCode, "Exception",
					ExceptionUtil.exception_details(e));
			return mvr;
		}
	}

	public MessageValidationResults validateLastUpload(ValidationContext vc) {
		byte[] message = session.getlastUpload();
		byte[] input2 = session.getlastUpload2();
		vc.privKeyPassword = session.getPassword2();
		GwtErrorRecorderBuilder gerb = new GwtErrorRecorderBuilder();
		if (input2 != null && input2.length <= 2) {
			// input looks like empty file name
			input2 = null;
		}
		ValidateMessageService vm = new ValidateMessageService(null);
		return vm.runValidation(vc, message, input2, gerb);
	}

   public List <SimulatorStats> getSimulatorStats(List <SimId> simIds) throws IOException, NoSimException {
      logger.debug(session.id() + ": " + "getSimulatorStats for " + simIds);
      try {
         List <SimulatorStats> stats = new ArrayList <>();
         for (SimId simId : simIds) {
            SimDb db = new SimDb(simId);
            if (db.getSimulatorActorType() == ActorType.REGISTRY) {
               stats.add(RegistryActorSimulator.getSimulatorStats(simId));
            } else if (db.getSimulatorActorType() == ActorType.REPOSITORY) {
               stats.add(RepositoryActorSimulator.getSimulatorStats(simId));
            } else if  (db.getSimulatorActorType() == ActorType.ONDEMAND_DOCUMENT_SOURCE) {
				stats.add(OddsActorSimulator.getSimulatorStats(simId));
			} else if (db.getSimulatorActorType() == ActorType.REPOSITORY_REGISTRY) {
               SimulatorStats rep = RepositoryActorSimulator.getSimulatorStats(simId);
               SimulatorStats reg = RegistryActorSimulator.getSimulatorStats(simId);
               rep.add(reg);
               stats.add(rep);
            } else if (db.getSimulatorActorType() == ActorType.DOCUMENT_RECIPIENT) {
               SimulatorStats rep = RepositoryActorSimulator.getSimulatorStats(simId);
               SimulatorStats reg = RegistryActorSimulator.getSimulatorStats(simId);
               rep.add(reg);
               stats.add(rep);
            } else if (db.getSimulatorActorType() == ActorType.RESPONDING_GATEWAY) {
               SimulatorStats rep = RepositoryActorSimulator.getSimulatorStats(simId);
               SimulatorStats reg = RegistryActorSimulator.getSimulatorStats(simId);
               rep.add(reg);
               stats.add(rep);
            } else if (db.getSimulatorActorType() == ActorType.COMBINED_RESPONDING_GATEWAY) {
               SimulatorStats rep = RepositoryActorSimulator.getSimulatorStats(simId);
               SimulatorStats reg = RegistryActorSimulator.getSimulatorStats(simId);
               rep.add(reg);
               stats.add(rep);
            } else {
               stats.add(new SimulatorStats(simId));
               logger.debug("Don't recognize actorType - " + db.getSimulatorActorType());
            }
         }
         return stats;
      } catch (Exception e) {
         logger.error(ExceptionUtil.exception_details(e, "getSimulatorStats"));
         throw e;
      }
   }

	public List<Pid> getPatientIds(SimId simId) throws IOException, NoSimException {
		SimDb db = new SimDb(simId);
		return db.getAllPatientIds();
	}

	public String addPatientIds(SimId simId, List<Pid> patientIds) throws IOException, NoSimException {
		SimDb db = new SimDb(simId);
		for (Pid pid : patientIds)
			db.addPatientId(pid);
		return null;
	}

	public boolean deletePatientIds(SimId simId, List<Pid> patientIds) throws IOException, NoSimException {
		return new SimDb(simId).deletePatientIds(patientIds);
	}

	public Result getSimulatorEventRequestAsResult(TransactionInstance ti) throws Exception {
		SimDb db = null;
		try {
			db = new SimDb(ti);
		} catch (Exception e) {
			throw new Exception("Cannot load simulator event - " + e.getMessage(), e);
		}
		File reqeustFile = db.getRequestBodyFile();
		if (reqeustFile == null) return null;
		Metadata m = null;
		try {
			m = MetadataParser.parseContent(reqeustFile);
		} catch (Exception e) {
			return ResultBuilder.RESULT(new Metadata());
//			throw new Exception("Cannot load simulator event - " + e.getMessage(), e);
		}
		return ResultBuilder.RESULT(m);
	}

	public Result getSimulatorEventResponseAsResult(TransactionInstance ti) throws Exception {
		SimDb db = null;
		try {
			db = new SimDb(ti);
		} catch (Exception e) {
			throw new Exception("Cannot load simulator event - " + e.getMessage(), e);
		}
		if (!db.responseBodyExists()) return null;
		String response = db.getResponseBody();
		Metadata m = null;
		try {
			m = MetadataParser.parseContent(null, response);
		} catch (Exception e) {
			return ResultBuilder.RESULT(new Metadata());
//			throw new Exception("Cannot load simulator event - " + e.getMessage(), e);
		}
		return ResultBuilder.RESULT(m);
	}


}
