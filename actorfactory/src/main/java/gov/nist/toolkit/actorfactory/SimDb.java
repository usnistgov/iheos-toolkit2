package gov.nist.toolkit.actorfactory;

import gov.nist.toolkit.actorfactory.client.*;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.actortransaction.client.TransactionInstance;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.http.HttpHeader.HttpHeaderParseException;
import gov.nist.toolkit.http.HttpMessage;
import gov.nist.toolkit.http.HttpParseException;
import gov.nist.toolkit.http.HttpParser;
import gov.nist.toolkit.http.ParseException;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.simcommon.server.ExtendedPropertyManager;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.utilities.io.ZipDir;
import gov.nist.toolkit.xdsexception.ToolkitRuntimeException;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * Each simulator has an on-disk presence that keeps track of its long
 * term status and a log of its input/output messages. This class
 * represents that on-disk presence.
 * 
 * Simulators are created through the factory ActorSimulatorFactory and their
 * configurations are managed through ActorSimulatorConfig class.
 */
public class SimDb {
	private final PidDb pidDb = new PidDb(this);
	SimId simId = null;    // ip is the simulator id
	File dbRoot = null;  // base of the simulator db
	String event = null;
	File simDir = null;   // directory within simdb that represents this event
	String actor = null;
	String transaction = null;
	File transactionDir = null;  
	static Logger logger = Logger.getLogger(SimDb.class);


	static public SimDb mkSim(SimId simid, String actor) throws IOException, NoSimException {
        validateSimId(simid);
		return mkSim(Installation.installation().simDbFile(), simid, actor);
	}

	static public SimDb mkSim(File dbRoot, SimId simid, String actor) throws IOException, NoSimException {
        validateSimId(simid);
		if (!dbRoot.exists())
			dbRoot.mkdir();
		if (!dbRoot.canWrite() || !dbRoot.isDirectory())
			throw new IOException("Simulator database location, " + dbRoot.toString() + " is not a directory or cannot be written to");

		File simActorDir = new File(dbRoot.getAbsolutePath() + File.separatorChar + simid + File.separatorChar + actor);
		simActorDir.mkdirs();
		if (!simActorDir.exists()) {
			logger.error("Simulator " + simid + ", " + actor + " cannot be created");
			throw new IOException("Simulator " + simid + ", " + actor + " cannot be created");
		}

		SimDb db = new SimDb(dbRoot, simid, actor, null);
		db.setSimulatorType(actor);
		return db;
	}
	
	public SimDb() {
		dbRoot = Installation.installation().simDbFile();
	}
	
	public SimDb(SimId simulatorId) throws IOException, NoSimException {
		this(Installation.installation().simDbFile(), simulatorId, null, null);
	}

	public boolean exists(SimId simId) {
		return new File(Installation.installation().simDbFile(), simId.toString()).exists();
	}

	public SimDb(File dbRoot, SimId simId) throws IOException, NoSimException {
		this.simId = simId;
        validateSimId();
		if (simId == null)
			throw new ToolkitRuntimeException("SimDb - cannot build SimDb with null simId");
		this.dbRoot = dbRoot;

		if (!dbRoot.canWrite() || !dbRoot.isDirectory())
			throw new IOException("Simulator database location, [" + dbRoot.toString() + "] is not a directory or cannot be written to");

		String ipdir = simId.toString();
		simDir = new File(dbRoot.toString()  /*.getAbsolutePath()*/ + File.separatorChar + ipdir);
		if (!simDir.exists()) {
			logger.error("Simulator " + simId + " does not exist (" + simDir + ")");
			throw new NoSimException("Simulator " + simId + " does not exist (" + simDir + ")");
		}

		simDir.mkdirs();

		if (!simDir.isDirectory())
			throw new IOException("Cannot create content in Simulator database, creation of " + simDir.toString() + " failed");

		// add this for safety when deleting simulators
		Io.stringToFile(simSafetyFile(), simId.toString());
	}

	public PidDb getPidDb() { return pidDb; }

    static void validateSimId(SimId simId) throws IOException {
        String badChars = " \t\n<>{}.";
        for (int i=0; i<badChars.length(); i++) {
            char c = badChars.charAt(i);
            int ind = -1;
            if ((ind = simId.getId().indexOf(c)) != -1) throw new IOException(String.format("Simulator ID contains bad character at position %s", ind));
            if ((ind = simId.getId().indexOf(c)) != -1) throw new IOException(String.format("Simulator User (testSession) contains bad character at position %s", i));
        }
    }

    void validateSimId() throws IOException { validateSimId(simId);}

	File simSafetyFile() { return new File(simDir, "simId.txt"); }
	boolean isSim() { return new File(simDir, "simId.txt").exists(); }
    boolean isSimDir(File dir) { return new File(dir, "simId.txt").exists(); /*&& new File(simDir, "simctl.ser").exists();*/ }

	// ipAddr aka simid
	public SimDb(File dbRoot, SimId simId, String actor, String transaction) throws IOException, NoSimException {
		this(dbRoot, simId);

		this.actor = actor;
		this.transaction = transaction;

		if (actor != null && transaction != null) {
			String transdir = simDir + File.separator + actor + File.separator + transaction;
			transactionDir = new File(transdir);
			transactionDir.mkdirs();
			if (!transactionDir.isDirectory())
				throw new IOException("Cannot create content in Simulator database, creation of " + transactionDir + " failed");
		} else
            return;

		Date date = new Date();

		event = asFilenameBase(date);

		File eventDir = new File(transactionDir, event);
		eventDir.mkdirs();
		Serialize.out(new File(eventDir, "date.ser"), date);

	}

	public SimDb(TransactionInstance ti) throws IOException, NoSimException, BadSimIdException {
		this(Installation.installation().simDbFile(), new SimId(ti.simId));

		this.actor = ti.actorType.getShortName();
		this.transaction = ti.name;

		if (actor != null && transaction != null) {
			String transdir = simDir + File.separator + actor + File.separator + transaction;
			transactionDir = new File(transdir);
			transactionDir.mkdirs();
			if (!transactionDir.isDirectory())
				throw new IOException("Cannot create content in Simulator database, creation of " + transactionDir + " failed");
		}
		event = ti.label;
	}

	// actor, transaction, and event must be filled in
	public Date getEventDate() throws IOException, ClassNotFoundException {
		if (transactionDir == null || event == null) return null;
		File eventDir = new File(transactionDir, event);
		return (Date) Serialize.in(new File(eventDir, "date.ser"));
	}

	public File getRoot() { return dbRoot; }
	
	public void delete() {
		if (isSim())
			delete(simDir);
	}
	
	public List<String> getActorsForSimulator() {
		List<String> actors = new ArrayList<>();
		File[] files = simDir.listFiles();
		for (File file : files) {
			if (file.isDirectory())
				actors.add(file.getName());
		}
		return actors;
	}
	
	static public Date getNewExpiration(@SuppressWarnings("rawtypes") Class controllingClass)   {
		// establish expiration for newly touched cache elements
		Date now = new Date();
		Calendar newExpiration = Calendar.getInstance();
		newExpiration.setTime(now);
		
		String dayOffset = ExtendedPropertyManager.getProperty(controllingClass, "expiration");
		if (dayOffset == null) {
//			logger.error("Extended Property expiration of class " + controllingClass + " is not defined");
			dayOffset = "1";
		}
		newExpiration.add(Calendar.DAY_OF_MONTH, Integer.parseInt(dayOffset));
		return newExpiration.getTime();
	}

	static public void deleteAllSims() throws IOException, NoSimException {
		SimDb simDb = new SimDb();
		List<SimId> allSimIds = simDb.getAllSimIds();
		for (SimId simId : allSimIds) {
			SimDb db = new SimDb(simId);
			db.delete();
		}
	}

    static public void deleteSims(List<SimId> simIds) throws IOException {
        for (SimId simId : simIds) {
            logger.info("Deleting sim " + simId);
            try {
                SimDb db = new SimDb(simId);
                db.delete();
            } catch (NoSimException e) { } // ignore
        }
    }
	
	public List<SimId> getAllSimIds() throws BadSimIdException {
		File[] files = dbRoot.listFiles();
		List<SimId> ids = new ArrayList<>();
		if (files == null) return ids;

		for (File dir : files) {
			if (isSimDir(dir))
				ids.add(new SimId(dir.getName()));
		}
		return ids;
	}

    public List<SimId> getSimIdsForUser(String user) throws BadSimIdException {
        List<SimId> ids = getAllSimIds();
        List<SimId> selectedIds = new ArrayList<>();
        for (SimId id : ids) {
            if (user.equals(id.getUser()))
                selectedIds.add(id);
        }
        return selectedIds;
    }

	/**
	 * Get a simulator.
	 * @return simulator if it exists or null
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public SimulatorConfig getSimulator(SimId simId) throws IOException, ClassNotFoundException {
		SimulatorConfig config = null;
		try {
			config = GenericSimulatorFactory.loadSimulator(simId, true);
		} catch (NoSimException e) { // cannot actually happen give parameters
		}
		return config;
	}

	public File getSimulatorControlFile() {
		return new File(simDir.toString() + File.separatorChar + "simctl.ser");
	}
	
	public static String getTransactionDirName(TransactionType tt)  {
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

	//
	// Patient Id Feed support
	//

	// These next methods manage the storage of Patient IDs received through
	// the Patient Identity Feed.
	// All these require the new SimDb() be called with SimId parameter
	// The other class that manages patient ids is PifHandler

	public List<Pid> getAllPatientIds() {
		return pidDb.getAllPatientIds();
	}

	String stripFileType(String filename, String filetype) {
		int dot = filename.lastIndexOf("." + filetype);
		if (dot == -1) return filename;
		return filename.substring(0, dot);
	}

	public File getAffinityDomainDir(String adOid) {
		return pidDb.getAffinityDomainDir(adOid);
	}

	public void addPatientId(Pid pid) throws IOException {
		pidDb.addPatientId(pid);
	}

	public void addPatientId(String patientId) throws IOException {
		pidDb.addPatientId(patientId);
	}

	public boolean deletePatientIds(List<Pid> toDelete) {
		return pidDb.deletePatientIds(toDelete);
	}

	public boolean patientIdExists(Pid pid) throws IOException {
		return pidDb.patientIdExists(pid);
	}
		//
	//
	//


	//	public void setSimulatorType(String type) throws IOException {
	//		File simType = new File(getDBFilePrefix(fileNameBase) + File.separator + "sim_type.txt");
	//		Io.stringToFile(simType, type);
	//	}
	
	public ActorType getSimulatorActorType() {
		File typeFile = new File(simDir, "sim_type.txt");
		String name = null;
		try {
			name = Io.stringFromFile(typeFile).trim();
		} catch (IOException e) {
			return null;
		}
		return ActorType.findActor(name);
	}

	static public List<SimId> getSimulatorIdsforActorType(ActorType actorType) throws IOException, NoSimException {
		SimDb db = new SimDb();
		List<SimId> allSimIds = db.getAllSimIds();
		List<SimId> simIdsOfType = new ArrayList<>();
		for (SimId simId : allSimIds) {
			if (actorType.equals(getSimulatorActorType(simId)))
				simIdsOfType.add(simId);
		}

		return simIdsOfType;
	}

	static public ActorType getSimulatorActorType(SimId simId) throws IOException, NoSimException {
		SimDb db = new SimDb(simId);
		if (db == null) return null;
		return db.getSimulatorActorType();
	}
	
	public List<String> getTransactionsForSimulator() {
		List<String> trans = new ArrayList<>();
		
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

	public String getSimulatorType() throws IOException {
		File simType = new File(simDir + File.separator + "sim_type.txt");
		return Io.stringFromFile(simType).trim();
	}

	public void setSimulatorType(String type) throws IOException {
		File simType = new File(simDir + File.separator + "sim_type.txt");
		Io.stringToFile(simType, type);
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

	public List<TransactionInstance> getTransInstances(String ignored_actor, String trans) {
		String event_save = event;
		File transDir_save = transactionDir;
		List<String> names = new ArrayList<String>();
		List<TransactionInstance> transList = new ArrayList<>();

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
					TransactionInstance t = new TransactionInstance();
					t.simId = simId.toString();
					t.actorType = ActorType.findActor(actor.getName());
					t.label = inst.getName();
					t.name = name;

					transactionDir = new File(actor, name);
					logger.debug("transaction dir is " + transactionDir);
					event = t.label;
					Date date = null;
					try {
						date = getEventDate();
					} catch (IOException e) {
					} catch (ClassNotFoundException e) {
					}
					if (date == null) continue;  // only interested in transactions that have dates
					t.labelInterpretedAsDate = (date == null) ? "oops" : date.toString();
					t.nameInterpretedAsTransactionType = TransactionType.find(t.name);
					logger.debug("Found " + t);
					transList.add(t);
				}
			}
		}

		Collections.sort(transList, new ReverseTransactionInstanceComparator());

		event = event_save;
		transactionDir = transDir_save;
		logger.debug("returning " + transList);
		return transList;
	}

	// this cannot be stuffed into TransactionInstance since that is a client class
	class ReverseTransactionInstanceComparator implements Comparator<TransactionInstance> {
		@Override
		public int compare(TransactionInstance s1, TransactionInstance s2) {
			return -s1.label.compareTo(s2.label);
		}
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

	public File getRequestHeaderFile(SimId simid, String actor, String trans, String event) {
		File dir = findEventDir(trans, event);
		if (dir == null)
			return null;
		return new File(dir + File.separator + "request_hdr.txt");
	}

	public File getResponseHeaderFile(SimId simid, String actor, String trans, String event) {
		File dir = findEventDir(trans, event);
		if (dir == null)
			return null;
		return new File(dir + File.separator + "response_hdr.txt");
	}

	public File getRequestBodyFile(SimId simid, String actor, String trans, String event) {
		File dir = findEventDir(trans, event);
		if (dir == null)
			return null;
		return new File(dir + File.separator + "request_body.bin");
	}

	public File getResponseBodyFile(SimId simid, String actor, String trans, String event) {
		File dir = findEventDir(trans, event);
		if (dir == null)
			return null;
		return new File(dir + File.separator + "response_body.txt");
	}

	public File getLogFile(SimId simid, String actor, String trans, String event) {
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
		return asFilenameBase(new Date());
	}

	public String asFilenameBase(Date date) {
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
		try {
			out.write(bytes);
		} finally {
			out.close();
		}
	}

	public void putRequestBodyFile(byte[] bytes) throws IOException {
		OutputStream out = new FileOutputStream(getRequestBodyFile());
		try {
			out.write(bytes);
		} finally {
			out.close();
		}
	}

	public void putResponse(HttpMessage msg) throws IOException {
		File hdrFile = getResponseHdrFile();
		String hdrs = msg.getHeadersAsString();
		OutputStream os = new FileOutputStream(hdrFile);
		try {
			os.write(hdrs.getBytes());
		} finally {
			os.close();
		}

		String body = msg.getBody();
		File bodyFile = getResponseBodyFile();
		os = new FileOutputStream(bodyFile);
		try {
			os.write(body.getBytes());
		} finally {
			os.close();
		}
	}

}
