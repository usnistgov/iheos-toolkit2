package gov.nist.toolkit.actorfactory;

import gov.nist.toolkit.actorfactory.client.NoSimException;
import gov.nist.toolkit.actortransaction.client.ATFactory;
import gov.nist.toolkit.actortransaction.client.ATFactory.ActorType;
import gov.nist.toolkit.actortransaction.client.ATFactory.TransactionType;
import gov.nist.toolkit.http.HttpHeader.HttpHeaderParseException;
import gov.nist.toolkit.http.HttpMessage;
import gov.nist.toolkit.http.HttpParseException;
import gov.nist.toolkit.http.HttpParser;
import gov.nist.toolkit.http.ParseException;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.simcommon.server.ExtendedPropertyManager;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.utilities.io.ZipDir;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Each simulator has an on-disk presence that keeps track of its long
 * term status and a log of its input/output messages. This class
 * represents that on-disk presence.
 * 
 * Simulators are created through the factory ActorSimulatorFactory and their
 * configurations are managed through ActorSimulatorConfig class.
 */
public class SimDb {
	String simId = null;    // ip is the simulator id
	File dbRoot = null;  // base of the simulator db
	String event = null;
	File simDir = null;   // directory within simdb that represents this event
	String actor = null;
	String transaction = null;
	File transactionDir = null;  
	static Logger logger = Logger.getLogger(SimDb.class);


	static public SimDb mkSim(File dbRoot, String simid, String actor) throws IOException, NoSimException {
		if (!dbRoot.canWrite() || !dbRoot.isDirectory())
			throw new IOException("Simulator database location, " + dbRoot.toString() + " is not a directory or cannot be written to");

		simid = simid.replaceAll("\\.", "_");    // dir name that should be acceptable on all system types
		File simActorDir = new File(dbRoot.getAbsolutePath() + File.separatorChar + simid + File.separatorChar + actor);
		simActorDir.mkdirs();
		if (!simActorDir.exists()) {
			logger.error("Simulator " + simid + ", " + actor + " cannot be created");
			throw new IOException("Simulator " + simid + ", " + actor + " cannot be created");
		}
		
		return new SimDb(dbRoot, simid, actor, null);

	}
	
	public SimDb() {

	}
	
	public SimDb(String simulatorId) throws IOException, NoSimException {
		this(Installation.installation().simDbFile(), simulatorId, null, null);
	}


	// ipAddr aka simid
	public SimDb(File dbRoot, String simId, String actor, String transaction) throws IOException, NoSimException {
		this.simId = simId;
		this.actor = actor;
		this.transaction = transaction;
		this.dbRoot = dbRoot;

		if (!dbRoot.canWrite() || !dbRoot.isDirectory())
			throw new IOException("Simulator database location, " + dbRoot.toString() + " is not a directory or cannot be written to");

		String ipdir = simId.replaceAll("\\.", "_");
		simDir = new File(dbRoot.toString()  /*.getAbsolutePath()*/ + File.separatorChar + ipdir);
		if (!simDir.exists()) {
			logger.error("Simulator " + simId + " does not exist");
			throw new NoSimException("Simulator " + simId + " does not exist");
		}
			
		simDir.mkdirs();

		if (!simDir.isDirectory())
			throw new IOException("Cannot create content in Simulator database, creation of " + simDir.toString() + " failed");

		if (actor != null && transaction != null) {
			String transdir = simDir + File.separator + actor + File.separator + transaction;
			transactionDir = new File(transdir);
			transactionDir.mkdirs();
			if (!transactionDir.isDirectory())
				throw new IOException("Cannot create content in Simulator database, creation of " + transactionDir + " failed");
		}

		event = nowAsFilenameBase();

	}
	
	public void delete() {
		delete(simDir);
	}
	
	public String getActorForSimulator() {
		File[] files = simDir.listFiles();
		for (File file : files) {
			if (file.isDirectory())
				return file.getName();
		}
		return null;
	}
	
	static public Date getNewExpiration(@SuppressWarnings("rawtypes") Class controllingClass)   {
		// establish expiration for newly touched cache elements
		Date now = new Date();
		Calendar newExpiration = Calendar.getInstance();
		newExpiration.setTime(now);
		
		String dayOffset = ExtendedPropertyManager.getProperty(controllingClass, "expiration");
		if (dayOffset == null) {
			logger.error("Extended Property expiration of class " + controllingClass + " is not defined");
			dayOffset = "1";
		}
		newExpiration.add(Calendar.DAY_OF_MONTH, Integer.parseInt(dayOffset));
		return newExpiration.getTime();
	}


	
	public List<String> getAllSimIds() {
		File[] files = dbRoot.listFiles();
		List<String> ids = new ArrayList<String>();
		
		for (File sim : files) {
			if (sim.isDirectory())
				ids.add(sim.getName());
		}
		
		return ids;
	}
	
	public File getSimulatorControlFile() {
		return new File(simDir.toString() + File.separatorChar + "simctl.ser");
	}
	
	public static String getTransactionDirName(ATFactory.TransactionType tt)  {
		return tt.getShortName();
	}
	
	public File getTransactionDir(TransactionType tt) {
		String trans = getTransactionDirName(tt);
		return new File(simDir 
				+ File.separator + actor
				+ File.separator + trans
		);
	}

	public File getRegistryObjectFile(String id) {
		if (id == null)
			return null;
		if (!id.startsWith("urn:uuid:"))
			return null;

		// version of uuid that could be used as filename
		String x = id.substring(9).replaceAll("-", "_");

		File registryDir = new File(getDBFilePrefix(event) + File.separator + "Registry");
		registryDir.mkdirs();

		return new File(registryDir.toString() + File.separator + x + ".xml");
	}

	public List<String> getTransactionNames(String actorType) {
		File transDir = new File(simDir.toString() + File.separator + actorType);
		List<String> names = new ArrayList<String>();

		try {
			for (File f : transDir.listFiles()) {
				if (f.isDirectory())
					names.add(f.getName());
			}
		} catch (Exception e) {}

		return names;
	}
	

	public File getRegistryIndexFile() {
		File regDir = new File(simDir.toString() + File.separator + actor);
		regDir.mkdirs();
		return new File(regDir.toString() + File.separator + "reg_db.ser");
	}

	public File getRepositoryIndexFile() {
		File regDir = new File(simDir.toString() + File.separator + actor);
		regDir.mkdirs();
		return new File(regDir.toString() + File.separator + "rep_db.ser");
	}


	//	public void setSimulatorType(String type) throws IOException {
	//		File simType = new File(getDBFilePrefix(fileNameBase) + File.separator + "sim_type.txt");
	//		Io.stringToFile(simType, type);
	//	}
	
	public ActorType getSimulatorActorType() {
		File aDir = new File(simDir.toString());
		for (File file : aDir.listFiles()) {
			if (file.isDirectory()) {
				String name = file.getName();
				return ActorType.findActor(name);
			}
		}
		return null;
	}
	
	public List<String> getTransactionsForSimulator() {
		List<String> trans = new ArrayList<String>();
		
		for (File actor : simDir.listFiles()) {
			if (!actor.isDirectory())
				continue;
			for (File tr : actor.listFiles()) {
				if (!tr.isDirectory())
					continue;
				trans.add(tr.getName());
			}
		}
		
		return trans;
	}

	
	// huh? nothing is creating this file
	public String getSimulatorType() throws IOException {
		File simType = new File(getDBFilePrefix(event) + File.separator + "sim_type.txt");
		return Io.stringFromFile(simType).trim();
	}

	public File getRepositoryDocumentFile(String documentId) {
		File repDirFile = new File(getDBFilePrefix(event) + File.separator + "Repository");
		repDirFile.mkdirs();
		File repDocFile = new File(repDirFile.toString() + File.separator + oidToFilename(documentId) + ".bin");
		return repDocFile;
	}

	String oidToFilename(String oid) {
		return oid.replaceAll("\\.", "_");
	}

	String filenameToOid(String filename) {
		return filename.replaceAll("_", ".");
	}

	public String getFileNameBase() {
		return event;
	}

	public void setFileNameBase(String base) {
		event = base;
	}
	
	public File getSimDir() {
		return getIpDir();
	}

	public File getIpDir() {
		return simDir;
	}

	public List<String> getTransInstances(String ignored_actor, String trans) {
		List<String> names = new ArrayList<String>();

		for (File actor : simDir.listFiles()) {
			if (!actor.isDirectory())
				continue;
			for (File tr : actor.listFiles()) {
				if (!tr.isDirectory())
					continue;
				String name = tr.getName();
				if (trans != null && !name.equals(trans) && !trans.equals(("All")))
					continue;
				for (File inst : tr.listFiles()) {
					if (!inst.isDirectory())
						continue;
					names.add(inst.getName() + " " + name);
				}
			}
		}
		
		String[] nameArray = names.toArray(new String[0]);
		java.util.Arrays.sort(nameArray);	

		
		List<String> returns = new ArrayList<String>();
		for (int i=nameArray.length-1; i>=0; i--)
			returns.add(nameArray[i]);
		
		return returns;
	}
	
	public File[] getTransInstanceFiles(String actor, String trans) {
		File dir = new File(simDir 
				+ File.separator + actor
				+ File.separator + trans
		);


		File[] files = dir.listFiles();
		return files;
	}

	File getDBFilePrefix(String event) {
		File f = new File(simDir 
				+ File.separator + actor
				+ File.separator + transaction
				+ File.separator + event
		);
		f.mkdirs();
		return f;
	}

	public File getResponseBodyFile() {
		return new File(getDBFilePrefix(event) + File.separator + "response_body.txt");
	}

	public File getResponseHdrFile() {
		return new File(getDBFilePrefix(event) + File.separator + "response_hdr.txt");
	}

	public File getRequestMsgHdrFile(String filenamebase) {
		return new File(getDBFilePrefix(filenamebase) + File.separator + "request_hdr.txt");
	}

	public File getRequestMsgBodyFile(String filenamebase) {
		return new File(getDBFilePrefix(filenamebase) + File.separator + "request_body.bin");
	}

	public File getResponseMsgHdrFile(String filenamebase) {
		return new File(getDBFilePrefix(filenamebase) + File.separator + "response_hdr.txt");
	}

	public File getResponseMsgBodyFile(String filenamebase) {
		return new File(getDBFilePrefix(filenamebase) + File.separator + "response_body.txt");
	}

	public String getRequestMessageHeader() throws IOException {
		return getRequestMessageHeader(event);
	}

	public String getRequestMessageHeader(String filenamebase) throws IOException {
		File f = getRequestMsgHdrFile(filenamebase);
		if (!f.exists())
			throw new IOException("SimDb: Simulator Database file " + f.toString() + " does not exist");
		return Io.stringFromFile(f);
	}

	public HttpMessage getParsedRequest() throws HttpParseException, ParseException, IOException, HttpHeaderParseException {
		HttpParser parser = new HttpParser(getRequestMessageHeader().getBytes());

		HttpMessage msg = parser.getHttpMessage();
		msg.setBody(new String(getRequestMessageBody()));
		return msg;
	}

	public String getResponseMessageHeader() throws IOException {
		return getResponseMessageHeader(event);
	}

	public String getResponseMessageHeader(String filenamebase) throws IOException {
		File f = getResponseMsgHdrFile(filenamebase);
		if (!f.exists())
			throw new IOException("SimDb: Simulator Database file " + f.toString() + " does not exist");
		return Io.stringFromFile(f);
	}

	public byte[] getRequestMessageBody() throws IOException {
		return getRequestMessageBody(event);
	}

	public byte[] getRequestMessageBody(String filenamebase) throws IOException {
		File f = getRequestMsgBodyFile(filenamebase);
		if (!f.exists())
			throw new IOException("SimDB: Do not understand filename " + f);
		return Io.bytesFromFile(f);
	}

	public byte[] getResponseMessageBody() throws IOException {
		return getResponseMessageBody(event);
	}

	public byte[] getResponseMessageBody(String filenamebase) throws IOException {
		File f = getResponseMsgBodyFile(filenamebase);
		if (!f.exists())
			throw new IOException("SimDB: Do not understand filename " + f);
		return Io.bytesFromFile(f);
	}

	public File getLogFile() {
		return new File(getDBFilePrefix(event) + File.separator + "log.txt");
	}
	
	public void getMessageLogZip(OutputStream os, String event) throws IOException {
		new ZipDir().toOutputStream(getDBFilePrefix(event).toString(), os);
	}

	public void delete(String fileNameBase) throws IOException {
		File f = getDBFilePrefix(fileNameBase);
		delete(f);
	}

	public void delete(File f) {
		Io.delete(f);
//		if (!f.exists())
//			return;
//		if (f.isDirectory()) {
//			logger.debug("Delete dir " + f);
//			String[] contents = f.list();
//			for (int i=0; i<contents.length; i++) 
//				delete(new File(f + File.separator + contents[i]));
//			f.delete();
//		} else if (f.isFile()){
//			logger.debug("Delete file " + f);
//			f.delete();
//		}
	}

	public void rename(String fileNameBase, String newFileNameBase) throws IOException {

		File from = getDBFilePrefix(fileNameBase);
		File to = getDBFilePrefix(newFileNameBase);
		boolean stat = from.renameTo(to);

		if (!stat)
			throw new IOException("Rename failed");

	}
	
	File findEventDir(String trans, String event) {
		for (File actor : simDir.listFiles()) {
			if (!actor.isDirectory())
				continue;
			File eventDir = new File(actor + File.separator + trans + File.separator + event);
			if (eventDir.exists() && eventDir.isDirectory())
				return eventDir;
		}
		return null;
	}

	public File getTransactionEvent(String simid, String actor, String trans, String event) {
		File dir = new File(simDir 
				+ File.separator + actor
				+ File.separator + trans
				+ File.separator + event
		);

		return dir;
	}

	public File getRequestHeaderFile(String simid, String actor, String trans, String event) {
//		File dir = new File(ipDir 
//				+ File.separator + actor
//				+ File.separator + trans
//				+ File.separator + event
//				+ File.separator + "request_hdr.txt"
//		);
//
//		return dir;
		
		File dir = findEventDir(trans, event);
		if (dir == null)
			return null;
		return new File(dir + File.separator + "request_hdr.txt");
	}

	public File getResponseHeaderFile(String simid, String actor, String trans, String event) {
//		File dir = new File(ipDir 
//				+ File.separator + actor
//				+ File.separator + trans
//				+ File.separator + event
//				+ File.separator + "response_hdr.txt"
//		);
//
//		return dir;
		File dir = findEventDir(trans, event);
		if (dir == null)
			return null;
		return new File(dir + File.separator + "response_hdr.txt");
	}

	public File getRequestBodyFile(String simid, String actor, String trans, String event) {
//		File dir = new File(ipDir 
//				+ File.separator + actor
//				+ File.separator + trans
//				+ File.separator + event
//				+ File.separator + "request_body.bin"
//		);
//
//		return dir;
		File dir = findEventDir(trans, event);
		if (dir == null)
			return null;
		return new File(dir + File.separator + "request_body.bin");
	}

	public File getResponseBodyFile(String simid, String actor, String trans, String event) {
//		File dir = new File(ipDir 
//				+ File.separator + actor
//				+ File.separator + trans
//				+ File.separator + event
//				+ File.separator + "response_body.txt"
//		);
//
//		return dir;
		File dir = findEventDir(trans, event);
		if (dir == null)
			return null;
		return new File(dir + File.separator + "response_body.txt");
	}

	public File getLogFile(String simid, String actor, String trans, String event) {
//		File dir = new File(ipDir 
//				+ File.separator + actor
//				+ File.separator + trans
//				+ File.separator + event
//				+ File.separator + "log.txt"
//		);
//
//		return dir;
		File dir = findEventDir(trans, event);
		if (dir == null)
			return null;
		return new File(dir + File.separator + "log.txt");
	}

	public List<String> getRegistryIds(String simid, String actor, String trans, String event) {
		List<String> ids = new ArrayList<String>();

		File dir = getTransactionEvent(simid, actor, trans, event);
		File registry = new File(dir.toString() + File.separator + "Registry");

		if (registry.exists()) {
			for (File f : registry.listFiles()) {
				String filename = f.getName();
				int dotI = filename.indexOf('.');
				if (dotI != -1) {
					String name = filename.substring(0, dotI);
					ids.add(name);
				}
			}
		}
		return ids;
	}

	public File getRequestHeaderFile() {
		return getRequestMsgHdrFile(event);
	}

	public File getRequestBodyFile() {
		return getRequestMsgBodyFile(event);
	}

	public String nowAsFilenameBase() {
		Date date = new Date();
		
		Calendar c  = Calendar.getInstance();
		c.setTime(date);
		
		String year = Integer.toString(c.get(Calendar.YEAR));
		String month = Integer.toString(c.get(Calendar.MONTH) + 1);
		if (month.length() == 1)
			month = "0" + month;
		String day = Integer.toString(c.get(Calendar.DAY_OF_MONTH));
		if (day.length() == 1 )
			day = "0" + day;
		String hour = Integer.toString(c.get(Calendar.HOUR_OF_DAY));
			if (hour.length() == 1)
				hour = "0" + hour;
		String minute = Integer.toString(c.get(Calendar.MINUTE));
		if (minute.length() == 1)
			minute = "0" + minute;
		String second = Integer.toString(c.get(Calendar.SECOND));
		if (second.length() == 1)
			second = "0" + second;
		String mili = Integer.toString(c.get(Calendar.MILLISECOND));
		if (mili.length() == 2)
			mili = "0" + mili;
		else if (mili.length() == 1)
			mili = "00" + mili;
		
		String dot = "_";
		
		String val =
			year +
			dot +
			month +
			dot +
			day + 
			dot +
			hour +
			dot +
			minute +
			dot +
			second +
			dot +
			mili
			;
		return val;

//		String value = date.toString();
//
//		return value.replaceAll(" ", "_").replaceAll(":", "_");

	}

	public void putRequestHeaderFile(byte[] bytes) throws IOException {
		File f = getRequestHeaderFile();
		OutputStream out = new FileOutputStream(f);
		out.write(bytes);
		out.close();
	}

	public void putRequestBodyFile(byte[] bytes) throws IOException {
		OutputStream out = new FileOutputStream(getRequestBodyFile());
		out.write(bytes);
		out.close();
	}

	public void putResponse(HttpMessage msg) throws IOException {
		File hdrFile = getResponseHdrFile();
		String hdrs = msg.getHeadersAsString();
		OutputStream os = new FileOutputStream(hdrFile);
		os.write(hdrs.getBytes());
		os.close();

		String body = msg.getBody();
		File bodyFile = getResponseBodyFile();
		os = new FileOutputStream(bodyFile);
		os.write(body.getBytes());
		os.close();
	}

}
