package gov.nist.toolkit.simcommon.server

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.actortransaction.client.TransactionInstance
import gov.nist.toolkit.configDatatypes.client.Pid
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.http.*
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.simcommon.client.BadSimIdException
import gov.nist.toolkit.simcommon.client.NoSimException
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.client.SimulatorConfig
import gov.nist.toolkit.utilities.io.Io
import gov.nist.toolkit.utilities.io.ZipDir
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException
import groovy.transform.TypeChecked
import org.apache.http.annotation.Obsolete
import org.apache.log4j.Logger
/**
 * Each simulator has an on-disk presence that keeps track of its long
 * term status and a log of its input/output messages. This class
 * represents that on-disk presence.
 *
 * Simulators are created through the factory ActorSimulatorFactory and their
 * configurations are managed through ActorSimulatorConfig class.
 */
@TypeChecked
public class SimDb {
	private final PidDb pidDb = new PidDb(this);
	SimId simId = null;    // ip is the simulator id
	//	private File dbRoot = null;  // base of the simulator db
	private String event = null;
	private File simDir = null;   // directory within simdb that represents this event
	private String actor = null;
	private String transaction = null;
	private File transactionDir = null;
	static private Logger logger = Logger.getLogger(SimDb.class);

	public SimDb mkSim(SimId simid, String actor) throws IOException, NoSimException {

		// if this is a FHIR sim there is a different factory method to use
		ActorType actorType = ActorType.findActor(actor);
		if (actorType == ActorType.FHIR_SERVER)
			simid.forFhir()
		if (simid.isFhir()) {
			return mkfSim(simid)
		}

		File dbRoot = getSimDbFile();
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

		SimDb db = new SimDb(simid, actor, null);
		db.setSimulatorType(actor);
		return db;
	}

	/**
	 * Given partial information (user and id) build the full simId
	 * @param simId1
	 * @return
	 */
	static SimId getFullSimId(SimId simId) {
		SimId ssimId = new SimId(simId.getUser(), simId.getId())
		if (exists(ssimId)) {
			// soap based sim
			SimDb simDb = new SimDb(ssimId)
			return simIdBuilder(simDb.getSimDir())
		} else {
			ssimId = ssimId.forFhir()
			if (exists(ssimId)) {
				// FHIR based sim
				SimDb simDb = new SimDb(ssimId)
				return simIdBuilder(simDb.getSimDir())
			}
		}
		throw new BadSimIdException("Simulator " + simId.toString() + " does not exist.")
	}


	/**
	 * Return base dir of SimDb storage
	 * or its FHIR equivalent if the SimId is from a FHIR sim
	 * @return
	 */
	static public File getSimDbFile(SimId simId) {
//		System.out.println("Using SimDb:getSimDbFile()");
		if (simId.isFhir())
			return Installation.instance().fhirSimDbFile();
		return Installation.instance().simDbFile();
	}

	static public File getSimDbFile() {
		return Installation.instance().simDbFile()
	}

	static public File getFSimDbFile() {
		return Installation.instance().fhirSimDbFile()
	}

	static boolean isSim(File simRoot) {
		isPrefix(simRoot, getSimDbFile())
	}

	static boolean isFSim(File simRoot) {
		isPrefix(simRoot, getFSimDbFile())
	}

	static boolean isPrefix(File file, File possiblePrefix) {
		file.getCanonicalPath().startsWith(possiblePrefix.getCanonicalPath())
	}

	/**
	 * Does simulator exist?
	 * Checks for existence of simdb directory for passed id.
	 * @param simId id of simulator to check
	 * @return boolean true if a simulator directory for this id exists in the
	 * simdb directory, false otherwise.
	 */
	static public boolean exists(SimId simId) {
		File f = new File(getSimDbFile(simId), simId.toString())
		return f.exists();
	}

	/**
	 * Base constructor Loads the simulator db directory 
	 */
	public SimDb() {
	}

	/**
	 * open existing sim
	 * @param simId
	 * @throws NoSimException
	 */
	public SimDb(SimId simId) throws NoSimException {
		File dbRoot = getSimDbFile(simId);
		this.simId = simId;
		validateSimId(simId);
		if (simId == null)
			throw new ToolkitRuntimeException("SimDb - cannot build SimDb with null simId");

		if (!dbRoot.exists())
			dbRoot.mkdirs();

		if (!dbRoot.canWrite() || !dbRoot.isDirectory())
			throw new ToolkitRuntimeException("Simulator database location, [" + dbRoot.toString() + "] is not a directory or cannot be written to");

		String ipdir = simId.toString();
		simDir = new File(dbRoot.toString()  /*.getAbsolutePath()*/ + File.separatorChar + ipdir);
		if (!simDir.exists()) {
			logger.error("Simulator " + simId + " does not exist (" + simDir + ")");
			throw new NoSimException("Simulator " + simId + " does not exist (" + simDir + ")");
		}

		simDir.mkdirs();

		if (!simDir.isDirectory())
			throw new ToolkitRuntimeException("Cannot create content in Simulator database, creation of " + simDir.toString() + " failed");

		// add this for safety when deleting simulators -
		Io.stringToFile(simSafetyFile(), simId.toString());
	}

	public PidDb getPidDb() { return pidDb; }

	public static void validateSimId(SimId simId) throws IOException {
		String badChars = " \t\n<>{}.";
		if (simId == null)
			throw new IOException("Simulator ID is null");
		String id = simId.getId();
		if (id != null) {
			for (int i = 0; i < badChars.length(); i++) {
				String c = new String(badChars.charAt(i));
				if (id.indexOf(c) != -1)
					throw new IOException(String.format("Simulator ID contains bad character at position %d", i));
				if (id.indexOf(c) != -1)
					throw new IOException(String.format("Simulator User (testSession) contains bad character at position %d", i));
			}
		}
	}

	private void validateCurrentSimId() throws IOException { validateSimId(simId);}

	private File simSafetyFile() { return new File(simDir, "simId.txt"); }
	public boolean isSim() {
		if (simDir == null) return false;
		return new File(simDir, "simId.txt").exists();
	}
	private static boolean isSimDir(File dir) { return new File(dir, "simId.txt").exists(); }

	/**
	 * create new sim
	 * @param simId
	 * @param actor
	 * @param transaction
	 * @throws IOException
	 * @throws NoSimException
	 */
	public SimDb(SimId simId, String actor, String transaction) throws IOException, NoSimException {
		this(simId);
		this.actor = actor;
		this.transaction = transaction;

		if (actor != null && transaction != null) {
//			String transdir = new File(new File(simDir, actor), transaction).path;
//			transactionDir = new File(transdir);
//			transactionDir.mkdirs();
//			if (!transactionDir.isDirectory())
//				throw new IOException("Cannot create content in Simulator database, creation of " + transactionDir + " failed");
			transactionDir = transactionDirectory(actor, transaction)
		} else
			return;

		Date date = new Date();
		File eventDir = mkEventDir(date);
		eventDir.mkdirs();
		Serialize.out(new File(eventDir, "date.ser"), date);
	}

	File transactionDirectory(String actor, String transaction) {
		assert actor
		assert transaction

		String transdir = new File(new File(simDir, actor), transaction).path;
		File dir = new File(transdir);
		dir.mkdirs();
		if (!dir.isDirectory())
			throw new IOException("Cannot create content in Simulator database, creation of " + transactionDir + " failed");
		return dir
	}

	public File mkEvent(String transaction) {
		Date date = new Date();
		File eventDir = mkEventDir(date);
		eventDir.mkdirs();
		return eventDir;
	}

	private File mkEventDir(Date date) {
		int incr = 0;
		String eventBase = Installation.asFilenameBase(date);
		while (true) {
			event = eventBase;
			if (incr != 0)
				event += '_' + incr;    // make unique
			File eventDir = getEventDir();  // from event
			if (eventDir.exists()) {
				// must be fresh new dir - try again
				incr++;
			}
			else
				break;
		}
		return getEventDir();
	}

	void openMostRecentEvent(String actor, String transaction) {
		transactionDir = transactionDirectory(actor, transaction)
		event = transactionDir.list().sort().last()
	}

	public String getEvent() { return event; }
	public void setEvent(String _event) { event = _event; }

	public File getEventDir() {
		return new File(transactionDir, event);
	}

	public void setClientIpAddess(String clientIpAddress) throws IOException {
		if (clientIpAddress != null) {
			Io.stringToFile(new File(getEventDir(), "ip.txt"), clientIpAddress);
		}
	}

	public SimDb(TransactionInstance ti) throws IOException, NoSimException, BadSimIdException {
		this(getFullSimId(new SimId(ti.simId)));

		this.actor = ti.actorType.getShortName();
		this.transaction = ti.trans;

		if (actor != null && transaction != null) {
			String transdir = new File(new File(simDir, actor), transaction).path;
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

	/**
	 * Delete simulator
	 */
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

	public void deleteAllSims() throws IOException, NoSimException {
		List<SimId> allSimIds = getAllSimIds();
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

	static SimId simIdBuilder(File simDefDir) {
		SimId simId = new SimId(simDefDir.name)
		if (isFSim(simDefDir)) simId.forFhir()
		simId.actorType = new SimDb(simId).getSimulatorType()
		simId
	}

	static List<SimId> getAllSimIds() throws BadSimIdException {

		List soapSimIds = getSimDbFile().listFiles().findAll { File file ->
			isSimDir(file)
		}.collect { File dir ->
			simIdBuilder (dir)
		}

		List fhirSimIds = getFSimDbFile().listFiles().findAll { File file ->
			isSimDir(file)
		}.collect { File dir ->
			simIdBuilder(dir)
		}

		(soapSimIds + fhirSimIds) as List<SimId>
	}

	/**
	 * should always use SimId - carries more information
	 * @return
	 */
	@Obsolete
	static List<String> getAllSimNames() {
		getAllSimIds().collect { it.toString()}
	}

	static List<SimId> getSimIdsForUser(String user) throws BadSimIdException {
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
	static public SimulatorConfig getSimulator(SimId simId) throws Exception {
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

	static public String getTransactionDirName(TransactionType tt)  {
		return tt.getShortName();
	}

	public File getTransactionDir(TransactionType tt) {
		String trans = getTransactionDirName(tt);
		return new File(new File(simDir, actor), trans)
	}

	public File getObjectFile(DbObjectType type, String id) {
		if (id == null)
			return null;
		if (!id.startsWith("urn:uuid:"))
			return null;

		// version of uuid that could be used as filename
		String x = id.substring(9).replaceAll("-", "_");

		File registryDir = new File(getDBFilePrefix(event), type.getName());
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

	public String getTransaction() { return transaction; }
	public String getActor() { return actor; }
	public SimId getSimId() { return simId; }

	private void getActorIfAvailable() {
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

	public List<SimId> getSimulatorIdsforActorType(ActorType actorType) throws IOException, NoSimException {
		List<SimId> allSimIds = getAllSimIds();
		List<SimId> simIdsOfType = new ArrayList<>();
		for (SimId simId : allSimIds) {
			if (actorType.equals(getSimulatorActorType(simId)))
				simIdsOfType.add(simId);
		}

		return simIdsOfType;
	}

	static public ActorType getSimulatorActorType(SimId simId) throws IOException, NoSimException {
		return new SimDb(simId).getSimulatorActorType();
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
		File simType = new File(simDir, "sim_type.txt");
		return Io.stringFromFile(simType).trim();
	}

	public void setSimulatorType(String type) throws IOException {
		File simType = new File(simDir, "sim_type.txt");
		Io.stringToFile(simType, type);
	}

	public File getRepositoryDocumentFile(String documentId) {
		File repDirFile = new File(getDBFilePrefix(event), "Repository");
		repDirFile.mkdirs();
		return new File(repDirFile.toString() + File.separator + oidToFilename(documentId) + ".bin");
	}

	private String oidToFilename(String oid) {
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

//		Collections.sort(transList, new ReverseTransactionInstanceComparator());

		transList = transList.sort { TransactionInstance ti -> ti.messageId }.reverse()

		event = event_save;
		transactionDir = transDir_save;
		logger.debug("returning " + transList);
		return transList;
	}

//	// this cannot be stuffed into TransactionInstance since that is a client class
//	class ReverseTransactionInstanceComparator implements Comparator<TransactionInstance> {
//		@Override
//		public int compare(TransactionInstance s1, TransactionInstance s2) {
//			return -s1.messageId.compareTo(s2.messageId);
//		}
//	}

	public File[] getTransInstanceFiles(String actor, String trans) {
		File dir = new File(new File(simDir, actor), trans)

		File[] files = dir.listFiles();
		return files;
	}

	private File getDBFilePrefix(String event) {
		File f = new File(new File(new File(simDir, actor), transaction), event)
		f.mkdirs();
		return f;
	}

	@Obsolete
	public File getResponseBodyFile() {
		return new File(getDBFilePrefix(event), "response_body.txt");
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
		return new File(getDBFilePrefix(event), RESPONSE_HEADER_FILE);
	}

	static final String REQUEST_HEADER_FILE = 'request_hdr.txt'
	static final String REQUEST_BODY_TXT_FILE = 'request_body.txt'
	static final String REQUEST_BODY_BIN_FILE = 'request_body.bin'
	static final String RESPONSE_HEADER_FILE = 'response_hdr.txt'
	static final String RESPONSE_BODY_TXT_FILE = 'response_body.txt'
	static final String REQUEST_URI_FILE = 'request_uri.txt'

	private File getRequestMsgHdrFile(String filenamebase) {
		return new File(getDBFilePrefix(filenamebase), REQUEST_HEADER_FILE);
	}

	File getRequestURIFile(String filenamebase) {
		return new File(getDBFilePrefix(filenamebase), REQUEST_URI_FILE)
	}

	private File getRequestMsgBodyFile(String filenamebase) {
		return new File(getDBFilePrefix(filenamebase), REQUEST_BODY_BIN_FILE);
	}

	private File getAlternateRequestMsgBodyFile(String filenamebase) {
		return new File(getDBFilePrefix(filenamebase), REQUEST_BODY_TXT_FILE);
	}

	private File getResponseMsgHdrFile(String filenamebase) {
		return new File(getDBFilePrefix(filenamebase), RESPONSE_HEADER_FILE);
	}

	@Obsolete
	private File getResponseMsgBodyFile(String filenamebase) {
		return new File(getDBFilePrefix(filenamebase), RESPONSE_BODY_TXT_FILE);
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

	public HttpMessage getParsedRequest() throws HttpParseException, ParseException, IOException, HttpHeader.HttpHeaderParseException {
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
		return new File(getDBFilePrefix(event), "log.txt");
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

	private File findEventDir(String trans, String event) {
		for (File actor : simDir.listFiles()) {
			if (!actor.isDirectory())
				continue;
			File eventDir = new File(new File(actor, trans), event)
			if (eventDir.exists() && eventDir.isDirectory())
				return eventDir;
		}
		return null;
	}



	public File getTransactionEvent(String simid, String actor, String trans, String event) {
		return new File(new File(new File(simDir, actor), trans), event)
	}

	public File getRequestHeaderFile(SimId simid, String actor, String trans, String event) {
		File dir = findEventDir(trans, event);
		if (dir == null)
			return null;
		return new File(dir, "request_hdr.txt");
	}

	public File getResponseHeaderFile(SimId simid, String actor, String trans, String event) {
		File dir = findEventDir(trans, event);
		if (dir == null)
			return null;
		return new File(dir, "response_hdr.txt");
	}

	public File getRequestBodyFile(SimId simid, String actor, String trans, String event) {
		File dir = findEventDir(trans, event);
		if (dir == null)
			return null;
		return new File(dir, "request_body.bin");
	}

	@Obsolete
	public File getResponseBodyFile(SimId simid, String actor, String trans, String event) {
		File dir = findEventDir(trans, event);
		if (dir == null)
			return null;
		return new File(dir, "response_body.txt");
	}

	public File getLogFile(SimId simid, String actor, String trans, String event) {
		File dir = findEventDir(trans, event);
		if (dir == null)
			return null;
		return new File(dir, "log.txt");
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

	File getRequestURIFile() {
		return getRequestURIFile(event)
	}

	public File getRequestHeaderFile() {
		return getRequestMsgHdrFile(event);
	}

	public File getRequestBodyFile() {
		return getRequestMsgBodyFile(event);
	}

	private File getAlternateRequestBodyFile() {
		return getAlternateRequestMsgBodyFile(event);
	}

	void putRequestURI(String uri) {
		File f = getRequestURIFile()
		OutputStream out = new FileOutputStream(f)
		try {
			out.write(uri.bytes);
		} finally {
			out.close();
		}
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


	/**************************************************************************
	 *
	 * FHIR Support
	 *
	 **************************************************************************/

	static final String BASE_TYPE = 'fhir'
	final static String ANY_TRANSACTION = 'any'

	/**
	 * Store a Resource in a sim
	 * @param resourceType  - index type (Patient...)
	 * @param resourceContents - JSON for index
	 * @return file where resource stored is ResDb
	 */
	File storeNewResource(String resourceType, String resourceContents, String id) {

		File resourceTypeDir = new File(getEventDir(), resourceType)

		resourceTypeDir.mkdirs()
		File file = new File(resourceTypeDir, "${id}.json")
		file.text = resourceContents
		return file
	}

	/**
	 * Return base dir of SimDb storage for FHIR resources (all FHIR simulators)
	 * This allows the inheritance to SimDb to work - SimDb actually manages
	 * both the SOAP simulators and the FHIR simulators.  This method controls
	 * which.
	 * @return
	 */

	static File getResDbFile() {
		return Installation.instance().fhirSimDbFile()
	}

	static String luceneIndexDirectoryName = 'simindex'

	/**
	 * Get location of Lucene index for this simulator
	 * @param simId
	 * @return
	 */
	static File getIndexFile(SimId simId) {
		return new File(getSimBase(simId), luceneIndexDirectoryName)
	}

	/**
	 * Base location of FHIR simulator
	 * @param simId - which simulator
	 * @return
	 */
	static File getSimBase(SimId simId) {
		return new File(getResDbFile(), simId.toString())
	}


	/**
	 * delete FHIR sim
	 * @param simId
	 * @return
	 */
	static boolean fdelete(SimId simId) {
		if (!fexists(simId)) return false
		Io.delete(getSimBase(simId))
		return true
	}

	/**
	 * Does fhir simulator exist?
	 * @param simId
	 * @return
	 */
	static  boolean fexists(SimId simId) {
		return getSimBase(simId).exists()
	}

	static SimDb mkfSim(SimId simid) throws IOException, NoSimException {
		return mkfSimi(getResDbFile(), simid, BASE_TYPE)
	}

	static SimDb mkfSim(SimId simid, String actor) throws IOException, NoSimException {
		return mkfSimi(getResDbFile(), simid, actor)
	}

	private static SimDb mkfSimi(File dbRoot, SimId simid, String actor) throws IOException, NoSimException {
		simid.forFhir()
		validateSimId(simid);
		if (!dbRoot.exists())
			dbRoot.mkdirs();
		if (!dbRoot.canWrite() || !dbRoot.isDirectory())
			throw new IOException("Resource Simulator database location, " + dbRoot.toString() + " is not a directory or cannot be written to");

		File simActorDir = new File(dbRoot.getAbsolutePath() + File.separatorChar + simid + File.separatorChar + actor);
		simActorDir.mkdirs();
		if (!simActorDir.exists()) {
			logger.error("Fhir Simulator " + simid + ", " + actor + " cannot be created");
			throw new IOException("Fhir Simulator " + simid + ", " + actor + " cannot be created");
		}

		SimDb db = new SimDb(simid, BASE_TYPE, null);
		db.setSimulatorType(actor);
		return db;
	}




}
