package gov.nist.toolkit.simulators.servlet;

import gov.nist.toolkit.actorfactory.*;
import gov.nist.toolkit.actorfactory.client.NoSimException;
import gov.nist.toolkit.actortransaction.shared.SimId;
import gov.nist.toolkit.simcommon.shared.config.SimulatorConfig;
import gov.nist.toolkit.actortransaction.shared.ATFactory;
import gov.nist.toolkit.configDatatypes.SimulatorProperties;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.GwtErrorRecorderBuilder;
import gov.nist.toolkit.http.HttpHeader;
import gov.nist.toolkit.http.HttpHeader.HttpHeaderParseException;
import gov.nist.toolkit.http.ParseException;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.simcommon.shared.config.SimulatorConfigElement;
import gov.nist.toolkit.simulators.sim.reg.store.MetadataCollection;
import gov.nist.toolkit.simulators.sim.reg.store.RegIndex;
import gov.nist.toolkit.simulators.sim.rep.RepIndex;
import gov.nist.toolkit.simulators.support.BaseDsActorSimulator;
import gov.nist.toolkit.simulators.support.DsSimCommon;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.sitemanagement.SeparateSiteLoader;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.soap.http.SoapFault;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.utilities.xml.OMFormatter;
import gov.nist.toolkit.valsupport.client.MessageValidationResults;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.DefaultValidationContextFactory;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.client.XdsException;
import org.apache.axiom.om.OMElement;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class SimServlet  extends HttpServlet {
	static Logger logger = Logger.getLogger(SimServlet.class);

	private static final long serialVersionUID = 1L;

	static ServletConfig config;
	Map<String, String> headers = new HashMap<String, String>();
	String contentType;
	HttpHeader contentTypeHeader;
	String bodyCharset;
	//	File simDbDir;
	MessageValidationResults mvr;
	PatientIdentityFeedServlet patientIdentityFeedServlet;


	@Override
	public void init(ServletConfig sConfig) throws ServletException {
		super.init(sConfig);
		config = sConfig;
		logger.info("Initializing toolkit in SimServlet");
		File warHome = new File(config.getServletContext().getRealPath("/"));
		logger.info("...warHome is " + warHome);
		Installation.instance().warHome(warHome);
		logger.info("...warHome initialized to " + Installation.instance().warHome());

		Installation.instance().setServletContextName(getServletContext().getContextPath());


		patientIdentityFeedServlet = new PatientIdentityFeedServlet();
		patientIdentityFeedServlet.init(config);

		onServiceStart();

		logger.info("SimServlet initialized");
	}

	@Override
	public void destroy() {
		onServiceStop();
	}


	public MessageValidationResults getMessageValidationResults() {
		return mvr;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		String uri = request.getRequestURI();
		logger.info("SIMSERVLET GET " + uri);
		String[] parts;
		try {
			int in = uri.indexOf("/del/");
			if (in != -1) {
				parts = uri.substring(in + "/del/".length()).split("\\/");
				handleDelete(response, parts);
				return;
			}
			in = uri.indexOf("/index/");
			if (in != -1) {
				parts = uri.substring(in + "/index/".length()).split("\\/");
				handleIndex(response, parts);
				return;
			}
			in = uri.indexOf("/message/");
			if (in != -1) {
				parts = uri.substring(in + "/message/".length()).split("\\/");
				handleMsgDownload(response, parts);
				return;
			}
			in = uri.indexOf("/siteconfig/");
			logger.info("siteconfig in is " + in);
			if (in != -1) {
				logger.info("working on siteconfig");
				parts = uri.substring(in + "/siteconfig/".length()).split("\\/");
				handleSiteDownload(response, parts);
				return;
			}
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		} catch (Exception e) {
			logger.error(ExceptionUtil.exception_details(e));
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}


	}

	void handleDelete(HttpServletResponse response, String[] parts) {
		String simid;
		String actor;
		String transaction;
		String message;

		try {
			simid = parts[0];
			actor = parts[1];
			transaction = parts[2];
			message = parts[3];
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		if (actor == null || actor.equals("null")) {
			try {
				SimDb sdb = new SimDb(Installation.instance().simDbFile(), new SimId(simid), null, null);
				actor = sdb.getActorsForSimulator().get(0);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NoSimException e) {
				e.printStackTrace();
			}
		}

		if (actor == null || actor.equals("null")) {
			logger.debug("No actor name found");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		SimDb db;
		try {
			db = new SimDb(Installation.instance().simDbFile(), new SimId(simid), actor, transaction);
		}
		catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		List<String> registryFilenames = db.getRegistryIds(simid, actor, transaction, message);
		List<String> registryUUIDs = new ArrayList<String>();
		for (String filename : registryFilenames) {
			registryUUIDs.add("urn:uuid:" + filename);
		}


		RegIndex regIndex = null;
		try {
			regIndex = getRegIndex(new SimId(simid));
		}
		catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		MetadataCollection mc = regIndex.mc;
		List<String> docUids = new ArrayList<String>();
		for (String id : registryUUIDs) {
			String uid = mc.deleteRo(id);
			if (uid != null)
				docUids.add(uid);
		}

		if (docUids.size() > 0) {
			RepIndex repIndex = null;
			try {
				repIndex = getRepIndex(new SimId(simid));
			}
			catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}

			for (String uid : docUids) {
				logger.debug("Delete document from index " + uid);
				repIndex.dc.delete(uid);
			}

		}


		logger.debug("Delete event " + simid + "/" + actor + "/" + transaction + "/" + message);
		File transEventFile = db.getTransactionEvent(simid, actor, transaction, message);
		db.delete(transEventFile);


		response.setStatus(HttpServletResponse.SC_OK);
	}

	// handle simulator message download
	void handleMsgDownload(HttpServletResponse response, String[] parts) {
		String simid;
		String actor;
		String transaction;
		String message;


		try {
			simid       = parts[0];
			actor       = parts[1];
			transaction = parts[2];
			message     = parts[3];
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		if (actor == null || actor.equals("null")) {
			try {
				SimDb sdb = new SimDb(Installation.instance().simDbFile(), new SimId(simid), null, null);
				actor = sdb.getActorsForSimulator().get(0);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NoSimException e) {
				e.printStackTrace();
			}
		}

		if (actor == null || actor.equals("null")) {
			logger.debug("No actor name found");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		SimDb db;
		try {
			db = new SimDb(Installation.instance().simDbFile(), new SimId(simid), actor, transaction);
			response.setContentType("application/zip");
			db.getMessageLogZip(response.getOutputStream(), message);
			response.getOutputStream().close();
			response.addHeader("Content-Disposition", "attachment; filename=" + message + ".zip");
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		response.setStatus(HttpServletResponse.SC_OK);
	}

	// handle simulator message download
	void handleSiteDownload(HttpServletResponse response, String[] parts) {
		String simid;

		try {
			simid       = parts[0];
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		logger.debug("site download of " + simid);

		if (simid == null || simid.trim().equals("")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		SimDb db;
		try {
			SimId simId = new SimId(simid);
			db = new SimDb(simId);
			SimulatorConfig config = db.getSimulator(simId);
			Site site = SimManager.getSite(config);
			OMElement siteEle = new SeparateSiteLoader().siteToXML(site);
			String siteString = new OMFormatter(siteEle).toString();
			logger.debug(siteString);

			response.setContentType("text/xml");
			response.getOutputStream().print(siteString);
			response.getOutputStream().close();
			response.addHeader("Content-Disposition", "attachment; filename=" + site.getName() + ".xml");
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		response.setStatus(HttpServletResponse.SC_OK);
	}


	// clean up index of an actor
	void handleIndex(HttpServletResponse response, String[] parts) {
		String simid;
		String actor;
		String transaction;

		try {
			simid = parts[0];
			actor = parts[1];
			transaction = parts[2];
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		if (actor == null || actor.equals("null")) {
			try {
				SimDb sdb = new SimDb(Installation.instance().simDbFile(), new SimId(simid), null, null);
				actor = sdb.getActorsForSimulator().get(0);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NoSimException e) {
				e.printStackTrace();
			}
		}

		if (actor == null || actor.equals("null")) {
			logger.debug("No actor name found");
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		SimDb db;
		try {
			db = new SimDb(Installation.instance().simDbFile(), new SimId(simid), actor, transaction);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		if (actor.equals("registry")) {
			RegIndex regIndex;
			try {
				regIndex = getRegIndex(new SimId(simid));
			}
			catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			MetadataCollection mc = regIndex.getMetadataCollection();

			// purge model in the index that are no longer present behind the index
			mc.purge();
		}

		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		String uri  = request.getRequestURI().toLowerCase();
		logger.info("+ + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + ");
		logger.info("uri is " + uri);
		logger.info("warHome is " + Installation.instance().warHome());
		RegIndex regIndex = null;
		RepIndex repIndex = null;
		ServletContext servletContext = config.getServletContext();
		boolean responseSent = false;

		Date now = new Date();

		//is client behind something?
		String ipAddress = request.getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) {
			ipAddress = request.getRemoteAddr();
		}

		String[] uriParts = uri.split("\\/");
		String toolkitServletName = (uriParts.length < 2) ? "" : uriParts[1];

		String endpointFormat = " - Endpoint format is http://" + request.getLocalName() + ":" + request.getLocalPort() + "/" + toolkitServletName + "/sim/simid/actor/transaction[/validation] " +
				"where simid, actor and transaction are variables for simulators. "  +
				"If validation is included, then this validation must be performed successfully for the transaction to be successful. " +
				" Validations are documented as part of tests that use them.";

		// endpoint parsing
		//
		// endpoint looks like
		// http://host:port/xdstools2/sim/simid/actor/transaction[/validation]
		// where
		//   simid is a uniqueID for a simulator
		//   actor
		//   transaction
		//   validation - name of a validation to be performed


		//
		// Parse endpoint to see which simulator/actor/transaction is target
		//
		int simIndex;

		for (simIndex=0; simIndex<uriParts.length; simIndex++) {
			if ("sim".equals(uriParts[simIndex]))
				break;
		}
		if (simIndex >= uriParts.length) {
			sendSoapFault(response, "Simulator: Do not understand endpoint http://" + request.getLocalName() + ":" + request.getLocalPort()  + uri + endpointFormat);
			return;
		}

		List<String> transIds = new ArrayList<String>();
		transIds.add("pnr");
		transIds.add("xcqr");

		SimId simid = null;
		String actor = null;
		String transaction = null;
		String validation = null;
		try {
			simid = new SimId(uriParts[simIndex + 1]);
			actor = uriParts[simIndex + 2];
			transaction = uriParts[simIndex + 3];
		}
		catch (Exception e) {
			sendSoapFault(response, "Simulator: Do not understand endpoint http://" + request.getLocalName() + ":" + request.getLocalPort() + uri + endpointFormat + " - " + e.getClass().getName() + ": " + e.getMessage());
			return;
		}

		try {
			validation = uriParts[simIndex+4];
		} catch (Exception e) {
			// ignore - null value will signal no validation
		}

		TransactionType transactionType = ATFactory.findTransactionByShortName(transaction);
		logger.debug("Incoming transaction is " + transaction);
		logger.debug("... which is " + transactionType);
		if (transactionType == null) {
			sendSoapFault(response, "Simulator: Do not understand the transaction requested by this endpoint (" + transaction + ") in http://" + request.getLocalName() + ":" + request.getLocalPort() + uri + endpointFormat);
			return;
		}




		boolean transactionOk = true;

		MessageValidatorEngine mvc = new MessageValidatorEngine();
		try {

			// DB space for this simulator
			SimDb db = new SimDb(Installation.instance().simDbFile(), simid, actor, transaction);
			request.setAttribute("SimDb", db);
			db.setClientIpAddess(ipAddress);

			logRequest(request, db, actor, transaction);

			SimulatorConfig asc = GenericSimulatorFactory.getSimConfig(Installation.instance().simDbFile(), simid);
			request.setAttribute("SimulatorConfig", asc);

			regIndex = getRegIndex(simid);
			repIndex = getRepIndex(simid);

			ValidationContext vc = DefaultValidationContextFactory.validationContext();
			vc.forceMtom = transactionType.isRequiresMtom();

			SimulatorConfigElement stsSce = asc.get(SimulatorProperties.requiresStsSaml);
			if (stsSce!=null && stsSce.hasBoolean() && stsSce.asBoolean())
				vc.requiresStsSaml = true;

			SimulatorConfigElement asce = asc.get(SimulatorProperties.codesEnvironment);
			if (asce != null)
				vc.setCodesFilename(asce.asString());

			SimCommon common= new SimCommon(db, request.isSecure(), vc, mvc, response);
			DsSimCommon dsSimCommon = new DsSimCommon(common, regIndex, repIndex);

			ErrorRecorder er = new GwtErrorRecorderBuilder().buildNewErrorRecorder();
			er.sectionHeading("Endpoint");
			er.detail("Endpoint is " + uri);
			mvc.addErrorRecorder("**** Web Service: " + uri, er);

			/////////////////////////////////////////////////////////////
			//
			// run the simulator for the requested actor/transaction pair
			//
			//////////////////////////////////////////////////////////////

			response.setContentType("application/soap+xml");

//			BaseDsActorSimulator sim = getSimulatorRuntime(simid);
			BaseDsActorSimulator sim = (BaseDsActorSimulator) RuntimeManager.getSimulatorRuntime(simid);

			sim.init(dsSimCommon, asc);
			if (asc.getConfigEle(SimulatorProperties.FORCE_FAULT).asBoolean()) {
				sendSoapFault(dsSimCommon, "Forced Fault");
				responseSent = true;
			} else {
				sim.onTransactionBegin(asc);
				transactionOk = sim.run(transactionType, mvc, validation);
				sim.onTransactionEnd(asc);
			}

			// Archive logs
			if (Installation.instance().propertyServiceManager().getArchiveLogs()) {
				SimDb simDb = dsSimCommon.simDb();
				if (simDb != null) {
					File eventDir = simDb.getEventDir();
					if (eventDir.exists()) {
						FileUtils.copyDirectory(eventDir, new File(Installation.instance().archive(), simDb.getEvent()));
					}
				}
			}


		}
		catch (InvocationTargetException e) {
			sendSoapFault(response, ExceptionUtil.exception_details(e));
			logger.error(ExceptionUtil.exception_details(e));
			responseSent = true;
		}
		catch (IllegalAccessException e) {
			sendSoapFault(response, ExceptionUtil.exception_details(e));
			logger.error(ExceptionUtil.exception_details(e));
			responseSent = true;
		}
		catch (InstantiationException e) {
			sendSoapFault(response, ExceptionUtil.exception_details(e));
			logger.error(ExceptionUtil.exception_details(e));
			responseSent = true;
		}
		catch (RuntimeException e) {
			sendSoapFault(response, ExceptionUtil.exception_details(e));
			logger.error(ExceptionUtil.exception_details(e));
			responseSent = true;
		}
		catch (IOException e) {
			sendSoapFault(response, ExceptionUtil.exception_details(e));
			logger.error(ExceptionUtil.exception_details(e));
			responseSent = true;
		}
		catch (HttpHeaderParseException e) {
			sendSoapFault(response, ExceptionUtil.exception_details(e));
			logger.error(ExceptionUtil.exception_details(e));
			responseSent = true;
		} catch (ClassNotFoundException e) {
			sendSoapFault(response, ExceptionUtil.exception_details(e));
			logger.error(ExceptionUtil.exception_details(e));
			responseSent = true;
		} catch (XdsException e) {
			sendSoapFault(response, ExceptionUtil.exception_details(e));
			logger.error(ExceptionUtil.exception_details(e));
			responseSent = true;
		} catch (NoSimException e) {
			sendSoapFault(response, ExceptionUtil.exception_details(e));
			logger.error(ExceptionUtil.exception_details(e));
			responseSent = true;
		} catch (ParseException e) {
			sendSoapFault(response, ExceptionUtil.exception_details(e));
			logger.error(ExceptionUtil.exception_details(e));
			responseSent = true;
		}
		catch (Exception e) {
			sendSoapFault(response, ExceptionUtil.exception_details(e));
			logger.error(ExceptionUtil.exception_details(e));
			responseSent = true;
		}
		finally {
			mvc.run();
			closeOut(response);
		}

		// Is mvc.run really required here again since it is already called in the Finally block?
		// Sunil.
		mvc.run();


		// this should go away after repository code made to use deltas
		if (!transactionOk) {
			synchronized(this) {
				// delete memory copy of indexes so they don't getRetrievedDocumentsModel written out
				servletContext.setAttribute("Rep_" + simid, null);
				repIndex = null;
			}
		}

		List<String> flushed = new ArrayList<String>();
		int regCacheCount = 0;
		int repCacheCount = 0;
		try {

			// Update disk copy of indexes
			if (repIndex != null) {
				repIndex.save();
			}

			logger.info("Starting Reg/Rep Cache cleanout");
			synchronized(this) {

				// check for indexes that are old enough they should be removed from cache
				for (@SuppressWarnings("unchecked")
					 Enumeration<String> en = (Enumeration<String>) servletContext.getAttributeNames(); en.hasMoreElements(); ) {
					String name = en.nextElement();
					if (name.startsWith("Reg_")) {
						RegIndex ri = (RegIndex) servletContext.getAttribute(name);
						if (ri.cacheExpires.before(now)) {
							logger.info("Unloading " + name);
							servletContext.removeAttribute(name);
							flushed.add(name);
						} else
							regCacheCount++;
					}
					if (name.startsWith("Rep_")) {
						RepIndex ri = (RepIndex) servletContext.getAttribute(name);
						if (ri.cacheExpires.before(now)) {
							logger.info("Unloading " + name);
							servletContext.removeAttribute(name);
							flushed.add(name);
						} else
							repCacheCount++;
					}
				}

			}
			logger.info("Done with Reg/Rep Cache cleanout");
		} catch (IOException e) {
			logger.info("Done with Reg/Rep Cache cleanout");
			if (!responseSent)
				sendSoapFault(response, ExceptionUtil.exception_details(e));
			e.printStackTrace();
		}

		logger.debug(regCacheCount + " items left in the Registry Index cache");
		logger.debug(repCacheCount + " items left in the Repository Index cache");

	} // EO doPost method

	private static void onServiceStart()  {
		try {
			SimDb db = new SimDb();
			List<SimId> simIds = db.getAllSimIds();
			for (SimId simId : simIds) {
				BaseDsActorSimulator sim = (BaseDsActorSimulator) RuntimeManager.getSimulatorRuntime(simId);
				if (sim == null) continue;
				DsSimCommon dsSimCommon = null;
				SimulatorConfig asc = GenericSimulatorFactory.getSimConfig(db.getRoot(), simId);
				sim.init(dsSimCommon, asc);
				sim.onServiceStart(asc);
			}
		} catch (Exception e) {
			logger.fatal(ExceptionUtil.exception_details(e));
		}
	}

	private static void onServiceStop() {
		try {
			SimDb db = new SimDb();
			List<SimId> simIds = db.getAllSimIds();
			for (SimId simId : simIds) {
				BaseDsActorSimulator sim = (BaseDsActorSimulator) RuntimeManager.getSimulatorRuntime(simId);
				if (sim == null) continue;
				DsSimCommon dsSimCommon = null;
				SimulatorConfig asc = GenericSimulatorFactory.getSimConfig(db.getRoot(), simId);
				sim.init(dsSimCommon, asc);
				sim.onServiceStop(asc);
			}
		} catch (Exception e) {
			logger.fatal(ExceptionUtil.exception_details(e));
		}
	}

	static public RegIndex getRegIndex(SimId simid) throws IOException, NoSimException {
		SimDb db = new SimDb(simid);
		ServletContext servletContext = config.getServletContext();
		String registryIndexFile = db.getRegistryIndexFile().toString();
		RegIndex regIndex;

		logger.info("GetRegIndex");
		synchronized(config) {
			regIndex = (RegIndex) servletContext.getAttribute("Reg_" + simid);
			if (regIndex == null) {
				logger.debug("Creating new RegIndex for " + simid);
				regIndex = new RegIndex(registryIndexFile, simid);
				regIndex.setSimDb(db);
				servletContext.setAttribute("Reg_" + simid, regIndex);
			} else
				logger.debug("Using cached RegIndex: " + simid + " db loc:" + regIndex.getSimDb().getRegistryIndexFile().toString());

			regIndex.cacheExpires = getNewExpiration();
		}

		return regIndex;
	}

	static public RepIndex getRepIndex(SimId simid) throws IOException, NoSimException {
		SimDb db = new SimDb(simid);
		ServletContext servletContext = config.getServletContext();
		String repositoryIndexFile = db.getRepositoryIndexFile().toString();
		RepIndex repIndex;

		logger.info("GetRepIndex");
		synchronized(config) {
			repIndex = (RepIndex) servletContext.getAttribute("Rep_" + simid);
			if (repIndex == null) {
				repIndex = new RepIndex(repositoryIndexFile, simid);
				servletContext.setAttribute("Rep_" + simid, repIndex);
			}

			repIndex.cacheExpires = getNewExpiration();
		}
		return repIndex;
	}

	// remove the index(s)
	static public void deleteSim(SimId simId) {
		if (config == null) return;
		ServletContext servletContext = config.getServletContext();
		servletContext.removeAttribute("Reg_" + simId);
		servletContext.removeAttribute("Rep_" + simId);
	}


	void closeOut(HttpServletResponse response) {
		try {
			response.getOutputStream().close();
		} catch (IOException e) {

		}
	}

	static Calendar getNewExpiration() {
		// establish expiration for newly touched cache elements
		Date now = new Date();
		Calendar newExpiration = Calendar.getInstance();
		newExpiration.setTime(now);
		newExpiration.add(Calendar.MINUTE, 15);
		return newExpiration;
	}


	private void sendSoapFault(HttpServletResponse response, String message) {
		try {
			SoapFault sf = new SoapFault(SoapFault.FaultCodes.Sender, message);
			SimCommon c = new SimCommon(response);
			DsSimCommon dsSimCommon = new DsSimCommon(c);
			OMElement faultEle = sf.getXML();
			logger.info("Sending SOAP Fault:\n" + new OMFormatter(faultEle).toString());
			OMElement soapEnv = dsSimCommon.wrapResponseInSoapEnvelope(faultEle);
			dsSimCommon.sendHttpResponse(soapEnv, SimCommon.getUnconnectedErrorRecorder(), false);
		} catch (Exception e) {
			logger.error(ExceptionUtil.exception_details(e));
		}
	}

	private void sendSoapFault(DsSimCommon dsSimCommon, String message) {
//        try {
		SoapFault sf = new SoapFault(SoapFault.FaultCodes.Sender, message);
		dsSimCommon.sendFault(sf);
//        } catch (Exception e) {
//            logger.error(ExceptionUtil.exception_details(e));
//        }
	}

	void logRequest(HttpServletRequest request, SimDb db, String actor, String transaction)
			throws FileNotFoundException, IOException, HttpHeaderParseException, ParseException {
		StringBuffer buf = new StringBuffer();

		buf.append(request.getMethod() + " " + request.getRequestURI() + " " + request.getProtocol() + "\r\n");
		for (Enumeration<String> en=request.getHeaderNames(); en.hasMoreElements(); ) {
			String name = en.nextElement();
			String value = request.getHeader(name);
			if (name.equals("Transfer-Encoding"))
				continue;  // log will not include transfer encoding so don't include this
			headers.put(name.toLowerCase(), value);
			buf.append(name).append(": ").append(value).append("\r\n");
		}
		//		bodyCharset = request.getCharacterEncoding();
		String ctype = headers.get("content-type");
		if (ctype == null || ctype.equals(""))
			throw new IOException("Content-Type header not found");
		contentTypeHeader = new HttpHeader("content-type: " + ctype);
		bodyCharset = contentTypeHeader.getParam("charset");
		contentType = contentTypeHeader.getValue();

		if (bodyCharset == null || bodyCharset.equals(""))
			bodyCharset = "UTF-8";

		buf.append("\r\n");


		db.putRequestHeaderFile(buf.toString().getBytes());

		db.putRequestBodyFile(Io.getBytesFromInputStream(request.getInputStream()));

	}
}
