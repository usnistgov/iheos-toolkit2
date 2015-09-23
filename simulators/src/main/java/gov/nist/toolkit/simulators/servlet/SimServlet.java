package gov.nist.toolkit.simulators.servlet;

import gov.nist.toolkit.actorfactory.*;
import gov.nist.toolkit.actorfactory.client.NoSimException;
import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ATFactory;
import gov.nist.toolkit.actortransaction.client.TransactionType;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.GwtErrorRecorderBuilder;
import gov.nist.toolkit.http.HttpHeader;
import gov.nist.toolkit.http.HttpHeader.HttpHeaderParseException;
import gov.nist.toolkit.http.ParseException;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
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
import gov.nist.toolkit.xdsexception.XdsException;
import org.apache.axiom.om.OMElement;
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
	File simDbDir;
	MessageValidationResults mvr;
	File warHome;
	PatientIdentityFeedServlet patientIdentityFeedServlet;


	public void init(ServletConfig sConfig) throws ServletException {
		super.init(sConfig);
		config = sConfig;
		logger.info("Initializing toolkit");
		warHome = new File(config.getServletContext().getRealPath("/"));
		logger.info("...warHome is " + warHome);
		Installation.installation().warHome(warHome);
		simDbDir = Installation.installation().simDbFile();
		logger.info("...simdb = " + simDbDir);

		patientIdentityFeedServlet = new PatientIdentityFeedServlet();
		patientIdentityFeedServlet.init(config);

		onServiceStart();

		logger.info("SimServlet initialized");
	}

	public void destroy() {
		onServiceStop();
	}


	public MessageValidationResults getMessageValidationResults() {
		return mvr;
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		String uri = request.getRequestURI();

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
			in = uri.indexOf("/site/");
			if (in != -1) {
				parts = uri.substring(in + "/site/".length()).split("\\/");
				handleSiteDownload(response, parts);
				return;
			}
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		} catch (Exception e) {
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
				SimDb sdb = new SimDb(simDbDir, new SimId(simid), null, null);
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
			db = new SimDb(simDbDir, new SimId(simid), actor, transaction);
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
				SimDb sdb = new SimDb(simDbDir, new SimId(simid), null, null);
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
			db = new SimDb(simDbDir, new SimId(simid), actor, transaction);
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
				SimDb sdb = new SimDb(simDbDir, new SimId(simid), null, null);
				actor = sdb.getActorsForSimulator().get(0);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NoSimException e) {
				// TODO Auto-generated catch block
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
			db = new SimDb(simDbDir, new SimId(simid), actor, transaction);
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

			// purge object in the index that are no longer present behind the index
			mc.purge();
		}

		response.setStatus(HttpServletResponse.SC_OK);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		String uri  = request.getRequestURI().toLowerCase();
		logger.info("+ + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + ");
		logger.info("uri is " + uri);
		logger.info("warHome is " + warHome);
		RegIndex regIndex = null;
		RepIndex repIndex = null;
		ServletContext servletContext = config.getServletContext();
		boolean responseSent = false;

		Date now = new Date();

		String[] uriParts = uri.split("\\/");
		String toolkitServletName = (uriParts.length < 2) ? "" : uriParts[1];

		String endpointFormat = " - Endpoint format is http://" + request.getLocalName() + ":" + request.getLocalPort() + "/" + toolkitServletName + "/sim/simid/actor/transaction[/validation] " +
				"where simid, actor and transaction are variables for simulators. "  +
				"If validation is included, then this validation must be performed successfully for the transaction to be successful. " +
				" Validations are documented as part of tests that use them.";

		// endpoint parsing
		//
		// endpoing looks like
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
		if (transactionType == null) {
			sendSoapFault(response, "Simulator: Do not understand the transaction requested by this endpoint (" + transaction + ") in http://" + request.getLocalName() + ":" + request.getLocalPort() + uri + endpointFormat);
			return;
		}




		boolean transactionOk = true;

		MessageValidatorEngine mvc = new MessageValidatorEngine();
		try {

			// DB space for this simulator
			SimDb db = new SimDb(simDbDir, simid, actor, transaction);
			request.setAttribute("SimDb", db);

			logRequest(request, db, actor, transaction);

			SimulatorConfig asc = GenericSimulatorFactory.getSimConfig(simDbDir, simid);

			regIndex = getRegIndex(simid);
			repIndex = getRepIndex(simid);

			ValidationContext vc = DefaultValidationContextFactory.validationContext();

			SimulatorConfigElement asce = asc.get(AbstractActorFactory.codesEnvironment);
			if (asce != null)
				vc.setCodesFilename(asce.asString());

			SimCommon common= new SimCommon(db, uri.startsWith("https"), vc, mvc, response);
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
			sim.onTransactionBegin(asc);
			transactionOk = sim.run(transactionType, mvc, validation);
			sim.onTransactionEnd(asc);

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
		finally {
			mvc.run();
			closeOut(response);
		}

		mvc.run();


		// this should go away after repository code made to use deltas
		if (!transactionOk) {
			synchronized(this) {
				// delete memory copy of indexes so they don't get written out
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
		} catch (IOException e) {
			if (!responseSent)
				sendSoapFault(response, ExceptionUtil.exception_details(e));
			e.printStackTrace();
		}

		logger.debug(regCacheCount + " items left in the Registry Index cache");
		logger.debug(repCacheCount + " items left in the Repository Index cache");

	}

	public static void onServiceStart()  {
		try {
			SimDb db = new SimDb();
			List<SimId> simIds = db.getAllSimIds();
			for (SimId simId : simIds) {
				BaseDsActorSimulator sim = (BaseDsActorSimulator) RuntimeManager.getSimulatorRuntime(simId);

				DsSimCommon dsSimCommon = null;
				SimulatorConfig asc = GenericSimulatorFactory.getSimConfig(db.getRoot(), simId);
				sim.init(dsSimCommon, asc);
				sim.onServiceStart(asc);
			}
		} catch (Exception e) {
			logger.fatal(ExceptionUtil.exception_details(e));
		}
	}

	public static void onServiceStop() {
		try {
			SimDb db = new SimDb();
			List<SimId> simIds = db.getAllSimIds();
			for (SimId simId : simIds) {
				BaseDsActorSimulator sim = (BaseDsActorSimulator) RuntimeManager.getSimulatorRuntime(simId);

				DsSimCommon dsSimCommon = null;
				SimulatorConfig asc = GenericSimulatorFactory.getSimConfig(db.getRoot(), simId);
				sim.init(dsSimCommon, asc);
				sim.onServiceStop(asc);
			}
		} catch (Exception e) {
			logger.fatal(ExceptionUtil.exception_details(e));
		}
	}

//	public static BaseDsActorSimulator getSimulatorRuntime(String simId) throws NoSimException, IOException, ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {
//		SimDb db = new SimDb();
//		SimulatorConfig config = GenericSimulatorFactory.getSimConfig(db.getRoot(), simId);
//		String actorTypeName = config.getType();
//		ActorType actorType = ActorType.findActor(actorTypeName);
//		String actorSimClassName = actorType.getSimulatorClassName();
//		logger.info("Starting sim " + simId + " of class " + actorSimClassName);
//		Class<?> clas = Class.forName(actorSimClassName);
//
//		// find correct constructor - no parameters
//		Constructor<?>[] constructors = clas.getConstructors();
//		Constructor<?> constructor = null;
//		for (int i=0; i<constructors.length; i++) {
//			Constructor<?> cons = constructors[i];
//			Class<?>[] parmTypes = cons.getParameterTypes();
//			if (parmTypes.length != 0) continue;
////				if (!parmTypes[0].getSimpleName().equals(dsSimCommon.getClass().getSimpleName())) continue;
////				if (!parmTypes[1].getSimpleName().equals(asc.getClass().getSimpleName())) continue;
//			constructor = cons;
//		}
//		if (constructor == null)
//			throw new ToolkitRuntimeException("Cannot find correct constructor for " + actorSimClassName);
//		Object obj = constructor.newInstance();
//		if (!(obj instanceof BaseDsActorSimulator)) {
//			throw new ToolkitRuntimeException("Received message for actor type " + actorTypeName + " which has a handler/simulator that does not extend AbstractDsActorSimulator");
//		}
//		return (BaseDsActorSimulator) obj;
//	}


	static public RegIndex getRegIndex(SimId simid) throws IOException, NoSimException {
		SimDb db = new SimDb(simid);
		ServletContext servletContext = config.getServletContext();
		String registryIndexFile = db.getRegistryIndexFile().toString();
		RegIndex regIndex;

		synchronized(config) {
			regIndex = (RegIndex) servletContext.getAttribute("Reg_" + simid);
			if (regIndex == null) {
				logger.debug("Creating new RegIndex for " + simid);
				regIndex = new RegIndex(registryIndexFile, simid);
				regIndex.setSimDb(db);
				servletContext.setAttribute("Reg_" + simid, regIndex);
			} else
				logger.debug("Using cached RegIndex");

			regIndex.cacheExpires = getNewExpiration();
		}

		return regIndex;
	}

	static public RepIndex getRepIndex(SimId simid) throws IOException, NoSimException {
		SimDb db = new SimDb(simid);
		ServletContext servletContext = config.getServletContext();
		String repositoryIndexFile = db.getRepositoryIndexFile().toString();
		RepIndex repIndex;

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
			OMElement soapEnv = dsSimCommon.wrapResponseInSoapEnvelope(faultEle);
			dsSimCommon.sendHttpResponse(soapEnv, SimCommon.getUnconnectedErrorRecorder(), false);
		} catch (Exception e) {
			logger.error(ExceptionUtil.exception_details(e));
		}
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
