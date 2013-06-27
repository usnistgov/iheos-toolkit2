package gov.nist.toolkit.xdstools2.server.simulator.support;

import gov.nist.toolkit.actorfactory.ActorFactory;
import gov.nist.toolkit.actorfactory.RegistryActorFactory;
import gov.nist.toolkit.actorfactory.SimCache;
import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.actorfactory.SimulatorFactory;
import gov.nist.toolkit.actorfactory.SiteServiceManager;
import gov.nist.toolkit.actorfactory.client.NoSimException;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ATFactory;
import gov.nist.toolkit.actortransaction.client.ATFactory.ActorType;
import gov.nist.toolkit.actortransaction.client.ATFactory.TransactionType;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.http.HttpHeader;
import gov.nist.toolkit.http.HttpHeader.HttpHeaderParseException;
import gov.nist.toolkit.http.ParseException;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.simulators.sim.ig.IgActorSimulator;
import gov.nist.toolkit.simulators.sim.recip.RecipientActorSimulator;
import gov.nist.toolkit.simulators.sim.reg.RegistryActorSimulator;
import gov.nist.toolkit.simulators.sim.reg.store.MetadataCollection;
import gov.nist.toolkit.simulators.sim.reg.store.RegIndex;
import gov.nist.toolkit.simulators.sim.rep.RepIndex;
import gov.nist.toolkit.simulators.sim.rep.RepositoryActorSimulator;
import gov.nist.toolkit.simulators.sim.rg.RGActorSimulator;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.soap.http.SoapFault;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.valsupport.client.MessageValidationResults;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.errrec.GwtErrorRecorderBuilder;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.XdsException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

public class SimServlet  extends HttpServlet {
	static Logger logger = Logger.getLogger(SimServlet.class);

	private static final long serialVersionUID = 1L;

	ServletConfig config;
	Map<String, String> headers = new HashMap<String, String>();
	String contentType;
	HttpHeader contentTypeHeader;
	String bodyCharset;
	byte[] bodyBytes;
	String body;
	File simDbDir;  // = "/Users/bill/tmp/xdstools2/simdb";
	MessageValidationResults mvr;
	File warHome;
	Session session;

//	enum SimType { RECIPIENT, REGISTRY};


	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		this.config = config;
		
		warHome = new File(config.getServletContext().getRealPath("/"));
		session = new Session(warHome, SiteServiceManager.getSiteServiceManager());
		simDbDir = Installation.installation().propertyServiceManager().getSimDbDir();

		
//		// this is being done for ToolkitServiceImpl - initialize the
//		// session caches by deleting the old ones (previous launch)
//		File sessionCaches = PerSessionCache.getSessionCaches(getServletContext());
//		Io.delete(sessionCaches);
//		sessionCaches.mkdirs();

		logger.info("SimServlet initialized");
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
				SimDb sdb = new SimDb(simDbDir, simid, null, null);
				actor = sdb.getActorForSimulator();
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
			db = new SimDb(simDbDir, simid, actor, transaction);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		List<String> registryFilenames = db.getRegistryIds(simid, actor, transaction, message);
		List<String> registryUUIDs = new ArrayList<String>();
		for (String filename : registryFilenames) {
			registryUUIDs.add("urn:uuid:" + filename);
		}


		RegIndex regIndex = getRegIndex(db, simid);

		MetadataCollection mc = regIndex.mc;
		List<String> docUids = new ArrayList<String>();
		for (String id : registryUUIDs) {
			String uid = mc.deleteRo(id);
			if (uid != null)
				docUids.add(uid);
		}

		if (docUids.size() > 0) {
			RepIndex repIndex = getRepIndex(db, simid);

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
				SimDb sdb = new SimDb(simDbDir, simid, null, null);
				actor = sdb.getActorForSimulator();
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
			db = new SimDb(simDbDir, simid, actor, transaction);
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
				SimDb sdb = new SimDb(simDbDir, simid, null, null);
				actor = sdb.getActorForSimulator();
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
			db = new SimDb(simDbDir, simid, actor, transaction);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		if (actor.equals("registry")) {
			RegIndex regIndex = getRegIndex(db, simid);
			MetadataCollection mc = regIndex.getMetadataCollection();
			
			// purge object in the index that are no longer present behind the index
			mc.purge();
		}

		response.setStatus(HttpServletResponse.SC_OK);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) {
//		String warHome = getServletContext().getRealPath("/");
//		System.setProperty("warHome", warHome);
//		logger.debug("warHome[SimServlet]: " + warHome + "\n");
//		if (dbPath == null) {
//			ToolkitServiceImpl tk = new ToolkitServiceImpl();
//			tk.setWarHome(warHome);
//			// force use of THIS reference to the servlet context
//			// since gwt-servlet may not be loaded yet
//			tk.propertyServiceManager.loadPropertyManager();
//			dbPath = tk.propertyServiceManager.getSimDbDir().toString();
//		}
		String uri  = request.getRequestURI().toLowerCase();
		logger.debug("uri is " + uri);
		logger.debug("warHome is " + warHome);
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

		String simid = null;
		String actor = null;
		String transaction = null;
		String validation = null;
		try {
			simid = uriParts[simIndex + 1];
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
//		Session session = new Session(warHome);
		try {

			// DB space for this simulator
			SimDb db = new SimDb(simDbDir, simid, actor, transaction);
			request.setAttribute("SimDb", db);

			logRequest(request, db, actor, transaction);

			SimulatorConfig asc = new SimulatorFactory(new SimCache().getSimManagerForSession(session.id())).getSimConfig(simDbDir, simid);

			regIndex = getRegIndex(db, simid);
			repIndex = getRepIndex(db, simid);


			
			ValidationContext vc = new ValidationContext();
			
			SimulatorConfigElement asce = asc.get(ActorFactory.codesEnvironment);
			if (asce != null)
				vc.setCodesFilename(asce.asString());
			
			
			
			SimCommon common= new SimCommon(db, uri.startsWith("https"), vc, mvc, regIndex, repIndex, response);
			
			ErrorRecorder er = new GwtErrorRecorderBuilder().buildNewErrorRecorder();
			er.sectionHeading("Endpoint");
			er.detail("Endpoint is " + uri);
			mvc.addErrorRecorder("Web Service", er);

			/////////////////////////////////////////////////////////////
			//
			// run the simulator for the requested actor/transaction pair
			//
			//////////////////////////////////////////////////////////////

			if (ActorType.REGISTRY.getShortName().equals(actor)) {
				
				SimulatorConfigElement updateConfig = asc.get(RegistryActorFactory.update_metadata_option);
				boolean updateEnabled = updateConfig.asBoolean();
				if (transactionType.equals(TransactionType.UPDATE) && !updateEnabled) {
					sendSoapFault(response, "Metadata Update not enabled on this actor " + uri + endpointFormat);
					return;
				}
				
				response.setContentType("application/soap+xml");

				common.regIndex = regIndex;
				RegistryActorSimulator reg = new RegistryActorSimulator(common, db, asc);
				transactionOk = reg.run(transactionType, mvc, validation);
				
			}
			else if (ActorType.RESPONDING_GATEWAY.getShortName().equals(actor)) {
				response.setContentType("application/soap+xml");

				RGActorSimulator rg = new RGActorSimulator(common, db, asc);
				transactionOk = rg.run(transactionType, mvc, validation);
				
			}
			else if (ActorType.INITIATING_GATEWAY.getShortName().equals(actor)) {
				response.setContentType("application/soap+xml");

				IgActorSimulator ig = new IgActorSimulator(common, db, asc);
				transactionOk = ig.run(transactionType, mvc, validation);
				
			}
			else if (ActorType.REPOSITORY.getShortName().equals(actor)) {
				
				String repositoryUniqueId = "";

				SimulatorConfigElement configEle = asc.get("repositoryUniqueId");
				if (configEle == null || configEle.asString() == null || configEle.asString().equals("")) {
					
				} else {
					repositoryUniqueId = configEle.asString();
				}

				RepositoryActorSimulator rg = new RepositoryActorSimulator(repIndex, common, db, asc, response, repositoryUniqueId);
				transactionOk = rg.run(transactionType, mvc, validation);
				
			}
			else if (ActorType.DOCUMENT_RECIPIENT.getShortName().equals(actor)) {
				
				RecipientActorSimulator rg = new RecipientActorSimulator(common, db, asc, response);
				transactionOk = rg.run(transactionType, mvc, validation);
				
			}
			else {
				sendSoapFault(response, "Simulator: Do not understand endpoint " + uri + ". Actor " + actor + " is not understood. " + endpointFormat);
				mvc.run();
				closeOut(response);
				return;
			}





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
	
	public RegIndex getRegIndex(SimDb db, String simid) {
		ServletContext servletContext = config.getServletContext(); 
		String registryIndexFile = db.getRegistryIndexFile().toString();
		RegIndex regIndex;

		synchronized(this) {
			regIndex = (RegIndex) servletContext.getAttribute("Reg_" + simid);
			if (regIndex == null) {
				regIndex = new RegIndex(registryIndexFile);
				regIndex.setSimDb(db);
				servletContext.setAttribute("Reg_" + simid, regIndex);
			}

			regIndex.cacheExpires = getNewExpiration();
		}

		return regIndex;
	}

	public RepIndex getRepIndex(SimDb db, String simid) {
		ServletContext servletContext = config.getServletContext(); 
		String repositoryIndexFile = db.getRepositoryIndexFile().toString();
		RepIndex repIndex;

		synchronized(this) {
			repIndex = (RepIndex) servletContext.getAttribute("Rep_" + simid);
			if (repIndex == null) {
				repIndex = new RepIndex(repositoryIndexFile);
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

	Calendar getNewExpiration() {
		// establish expiration for newly touched cache elements
		Date now = new Date();
		Calendar newExpiration = Calendar.getInstance();
		newExpiration.setTime(now);
		newExpiration.add(Calendar.MINUTE, 15);
		return newExpiration;
	}


	void sendSoapFault(HttpServletResponse response, String message) {
		try {
			SoapFault sf = new SoapFault(SoapFault.FaultCodes.Sender, message);
			SimCommon c = new SimCommon(response);
			OMElement faultEle = sf.getXML();
			OMElement soapEnv = c.wrapResponseInSoapEnvelope(faultEle);
			c.sendHttpResponse(soapEnv, SimCommon.getUnconnectedErrorRecorder(), false);
		} catch (Exception e) {
			logger.error(ExceptionUtil.exception_details(e));
		}
	}

//	Session getSession(HttpServletRequest request) {
//		HttpSession hsession = request.getSession();
//		Session session = (Session) hsession.getAttribute(ToolkitServiceImpl.sessionVarName);
//		if (session == null) {
//
//			session = new Session(getServletContext(), null, hsession.getId());
//			hsession.setAttribute(ToolkitServiceImpl.sessionVarName, session);
//		}
//
//		if (session.getIpAddr() == null) {
//			session.setIpAddr(request.getRemoteHost());
//		}
//
//		return session;
//	}
//	

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
