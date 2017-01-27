package gov.nist.toolkit.actorfactory;

import gov.nist.toolkit.actorfactory.client.BadSimIdException;
import gov.nist.toolkit.actorfactory.client.NoSimException;
import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.actortransaction.client.TransactionInstance;
import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.http.HttpHeader.HttpHeaderParseException;
import gov.nist.toolkit.http.HttpMessage;
import gov.nist.toolkit.http.HttpParseException;
import gov.nist.toolkit.http.HttpParser;
import gov.nist.toolkit.http.ParseException;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.utilities.io.ZipDir;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;
import org.apache.http.annotation.Obsolete;
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
	private File dbRoot = null;  // base of the simulator db
	private String event = null;
	private File simDir = null;   // directory within simdb that represents this event
	private String actor = null;
	private String transaction = null;
	private File transactionDir = null;
	static Logger logger = Logger.getLogger(SimDb.class);


	static public SimDb mkSim(SimId simid, String actor) throws IOException, NoSimException {
        validateSimId(simid);
		return mkSim(Installation.instance().simDbFile(), simid, actor);
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

   /**
    * Checks for existence of simdb directory for passed id.
    * @param simId id of simulator to check
    * @return boolean true if a simulator directory for this id exists in the
    * simdb directory, false otherwise.
    */
   public boolean exists(SimId simId) {
      return new File(Installation.instance().simDbFile(), simId.toString()).exists();
   }
	
	/**
	 * Base constructor Loads the simulator db directory 
	 */
	public SimDb() {
		dbRoot = Installation.instance().simDbFile();
	}
	
	public SimDb(SimId simulatorId) throws IOException, NoSimException {
		this(Installation.instance().simDbFile(), simulatorId, null, null);
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
    boolean isSimDir(File dir) { return new File(dir, "simId.txt").exists(); }

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

		event = Installation.asFilenameBase(date);

		File eventDir = getEventDir();
		eventDir.mkdirs();
		Serialize.out(new File(eventDir, "date.ser"), date);
	}

	public String getEvent() { return event; }

	public File getEventDir() {
		return new File(transactionDir, event);
	}

	public void setClientIpAddess(String clientIpAddress) throws IOException {
		if (clientIpAddress != null) {
			Io.stringToFile(new File(getEventDir(), "ip.txt"), clientIpAddress);
		}
	}

	public SimDb(TransactionInstance ti) throws IOException, NoSimException, BadSimIdException {
		this(Installation.instance().simDbFile(), new SimId(ti.simId));

		this.actor = ti.actorType.getShortName();
		this.transaction = ti.trans;

		if (actor != null && transaction != null) {
			String transdir = simDir + File.separator + actor + File.separator + transaction;
			transactionDir = new File(transdir);
			transactionDir.mkdirs();
			if (!transactionDir.isDirectory())
				throw new IOException("Cannot create content in Simulator database, creation of " + transactionDir + " failed");
		}
		event = ti.messageId;
	}

	// actor, transaction, and event must be filled in
	private Date getEventDate() throws IOException, ClassNotFoundException {
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
	public SimulatorConfig getSimulator(SimId simId) throws Exception {
		SimulatorConfig config = null;
		try {
			config = GenericSimulatorFactory.loadSimulator(simId, true);
		} catch (NoSimException e) { // cannot actually happen give parameters
		}
		return config;
	}

	public File getSimulatorControlFile() {
		return new File(simDir.toString() + File.separatorChar + "simctl.json");
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


	public void getActorIfAvailable() {
		if (actor==null) {
			try {
				String actorTemp = getSimulatorType();
				if (actorTemp!=null) {
					actor=actorTemp;
				}
			} catch (IOException ex) {
				logger.warn(ex.toString());
			}
		}
	}

	public File getRegistryIndexFile() {
		getActorIfAvailable();
		File regDir = new File(simDir.toString() + File.separator + actor);
		regDir.mkdirs();
		return new File(regDir.toString() + File.separator + "reg_db.ser");
	}

	public File getRepositoryIndexFile() {
		getActorIfAvailable();
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
					t.messageId = inst.getName();
					t.trans = name;

					transactionDir = new File(actor, name);
					logger.debug("transaction dir is " + transactionDir);
					event = t.messageId;
					Date date = null;
					try {
						date = getEventDate();
					} catch (IOException e) {
					} catch (ClassNotFoundException e) {
					}
					if (date == null) continue;  // only interested in transactions that have dates
					t.labelInterpretedAsDate = (date == null) ? "oops" : date.toString();
					t.nameInterpretedAsTransactionType = TransactionType.find(t.trans);

					String ipAddr = null;
					File ipAddrFile = new File(inst, "ip.txt");
					try {
						ipAddr = Io.stringFromFile(ipAddrFile);
						if (ipAddr != null && !ipAddr.equals("")) {
							t.ipAddress = ipAddr;
						}
					} catch (IOException e) {}

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
			return -s1.messageId.compareTo(s2.messageId);
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

	@Obsolete
	private File getResponseBodyFile() {
		return new File(getDBFilePrefix(event) + File.separator + "response_body.txt");
	}

	public void putResponseBody(String content) throws IOException {
		Io.stringToFile(getResponseBodyFile(), content);
	}

	public String getResponseBody() throws IOException {
		return Io.stringFromFile(getResponseBodyFile());
	}

	public boolean responseBodyExists() {
		return getResponseBodyFile().exists();
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

	public File getAlternateRequestMsgBodyFile(String filenamebase) {
		return new File(getDBFilePrefix(filenamebase) + File.separator + "request_body.txt");
	}

	public File getResponseMsgHdrFile(String filenamebase) {
		return new File(getDBFilePrefix(filenamebase) + File.separator + "response_hdr.txt");
	}

	@Obsolete
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

	@Obsolete
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

	public File getAlternateRequestBodyFile() {
		return getAlternateRequestMsgBodyFile(event);
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
		try {
			Io.stringToFile(getAlternateRequestBodyFile(), new String(bytes));
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
