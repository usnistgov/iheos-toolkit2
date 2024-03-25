package gov.nist.toolkit.simcommon.server

import gov.nist.toolkit.actortransaction.shared.ActorType
import gov.nist.toolkit.actortransaction.client.TransactionInstance
import gov.nist.toolkit.configDatatypes.client.Pid
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.errorrecording.GwtErrorRecorder
import gov.nist.toolkit.http.*
import gov.nist.toolkit.installation.server.Installation
import gov.nist.toolkit.installation.shared.TestSession
import gov.nist.toolkit.simcommon.client.*
//import gov.nist.toolkit.simcommon.server.index.SiTypeWrapper
//import gov.nist.toolkit.simcommon.server.index.SimIndex
import gov.nist.toolkit.utilities.io.Io
import gov.nist.toolkit.utilities.io.ZipDir
import gov.nist.toolkit.xdsexception.ExceptionUtil
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException
import groovy.transform.TypeChecked
import org.apache.commons.io.comparator.NameFileComparator
import org.apache.commons.io.filefilter.PrefixFileFilter
import org.apache.http.annotation.Obsolete
import java.util.logging.*
/*
import org.dcm4che3.hl7.HL7Parser
 */

import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.sax.SAXTransformerFactory
import javax.xml.transform.sax.TransformerHandler
import javax.xml.transform.stream.StreamResult
import java.nio.file.Files
import java.nio.file.Paths

//import org.apache.lucene.store.FSDirectory
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
	private Date eventDate;
	private File simDir = null;   // directory within simdb that represents this simulator
	private String actor = null;
	private String transaction = null;
	private File transactionDir = null;
	static private final Logger logger = Logger.getLogger(SimDb.class.getName());
//	static String luceneIndexDirectoryName = 'simindex'
	private TestSession testSession = null;
	static String simTypeFilename = 'sim_type.txt'
	private static final Object fileLock = new Object();

	static final String MARKER = 'MARKER';
	/**
	 * Base constructor Loads the simulator db directory
	 */
	SimDb() {}
	/**
	 * open existing sim
	 * @param simId
	 * @throws NoSimException
	 */
	SimDb(SimId simId) throws NoSimException {
		assert simId?.testSession?.value
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
			logger.severe("Simulator " + simId + " does not exist (" + simDir + ")");
			throw new NoSimException("Simulator " + simId + " does not exist (" + simDir + ")");
		}

		simDir.mkdirs();

		if (!simDir.isDirectory())
			throw new ToolkitRuntimeException("Cannot create content in Simulator database, creation of " + simDir.toString() + " failed");

		createSimSafetyFile()
	}
	SimDb(SimId simId, ActorType actor, TransactionType transaction, boolean openToLastTransaction) {
		this(simId, actor.shortName, transaction.shortName, openToLastTransaction)
	}

	SimDb(SimId simId, String actor, String transaction, boolean openToLastTransaction) {
		this(simId);
		assert actor
		this.actor = actor;
		this.transaction = transaction;

		if (actor != null && transaction != null) {
			transactionDir = transactionDirectory(actor, transaction)
		} else
			return;

		if (openToLastTransaction) {
			openMostRecentEvent(actor, transaction)
		} else {
			/**
			 * Fixes race condition
			 * @mratzenb Contributed code
			 * See https://github.com/usnistgov/iheos-toolkit2/issues/583
			 */
			File eventDir;
			synchronized (fileLock) {
				eventDate = new Date();
				eventDir = mkEventDir(eventDate);
				eventDir.mkdirs();
			}
			Serialize.out(new File(eventDir, "date.ser"), eventDate);
		}
	}



	SimDb mkSim(SimId simid, String actor) throws IOException, NoSimException {
		assert simid?.testSession?.value

		// if this is a FHIR sim there is a different factory method to use
		/*
		ActorType actorType = ActorType.findActor(actor);
		if (actorType == ActorType.FHIR_SERVER)
			simid.forFhir()
		if (simid.isFhir()) {
			return mkfSim(simid)
		}
		 */

		File dbRoot = getSimDbFile(simid.testSession);
		validateSimId(simid);
		if (!dbRoot.exists())
			dbRoot.mkdir();
		if (!dbRoot.canWrite() || !dbRoot.isDirectory())
			throw new IOException("Simulator database location, " + dbRoot.toString() + " is not a directory or cannot be written to");

		File simActorDir = new File(dbRoot.getAbsolutePath() + File.separatorChar + simid + File.separatorChar + actor);
		simActorDir.mkdirs();
		if (!simActorDir.exists()) {
			logger.severe("Simulator " + simid + ", " + actor + " cannot be created");
			throw new IOException("Simulator " + simid + ", " + actor + " cannot be created");
		}

		SimDb db = new SimDb(simid, actor, null, true);
//		db.setSimulatorType(actor);
		return db;
	}

	/**
	 * Given partial information (testSession and id) build the full simId
	 * @param simId1
	 * @return
	 */
	static SimId getFullSimId(SimId simId) {
		assert simId?.testSession?.value
		SimId ssimId = new SimId(simId.getTestSession(), simId.getId())
		if (exists(ssimId)) {
			// soap based sim
			SimDb simDb = new SimDb(ssimId)
			return internalSimIdBuilder(simDb.getSimDir(), simId.testSession)
		} else {
			ssimId = ssimId.forFhir()
			if (exists(ssimId)) {
				// FHIR based sim
				SimDb simDb = new SimDb(ssimId)
				return internalSimIdBuilder(simDb.getSimDir(), simId.testSession)
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
		assert simId?.testSession?.value
//		if (simId.isFhir())
//			return Installation.instance().fhirSimDbFile(simId.testSession);
		return Installation.instance().simDbFile(simId.testSession);
	}

	static public File getSimDbFile(TestSession testSession) {
		return Installation.instance().simDbFile(testSession)
	}

	static public File getFSimDbFile(TestSession testSession) {
		return Installation.instance().fhirSimDbFile(testSession)
	}

//	static boolean isSim(File simRoot) {
//		//isPrefix(simRoot, getSimDbFile())
//		!ActorType.findActor(new File(simRoot, simTypeFilename).text).isFhir()
//	}
//
//	static boolean isFSim(SimId simId) {
//		File base = getSimBase(simId)
//		return isFSim(base)
//	}

	static boolean isFSim(SimId simId) {
		//isPrefix(simRoot, getFSimDbFile())
		throw new Exception("isFSim - Unsupported option.")

		/*
		SimulatorConfig config
		try {
			config = AbstractActorFactory.getSimConfig(simId)
		} catch (Exception e) {
			return false
		}
		ActorType type = ActorType.findActor(config.actorType)
		type.isFhir()

		 */

		//ActorType.findActor(new File(simRoot, simTypeFilename).text).isFhir()
	}

//	static boolean isPrefix(File file, File possiblePrefix) {
//		file.getCanonicalPath().startsWith(possiblePrefix.getCanonicalPath())
//	}

//	List getAllResources(SimDbEvent event) {
//		setEvent(event)
//		getEventDir().listFiles().findAll { File f ->
//			f.isDirectory()
//		}.collect { File dir ->
//			String resourceType = dir.name
//			dir.listFiles().findAll { File rf ->
//				rf.name.endsWith('json')
//			}
//		}
//	}

	/**
	 * Does simulator exist?
	 * Checks for existence of simdb directory for passed id.
	 * @param simId id of simulator to check
	 * @return boolean true if a simulator directory for this id exists in the
	 * simdb directory, false otherwise.
	 */
	static public boolean exists(SimId simId) {
		File f = new File(getSimDbFile(simId), simId.toString())
        // Lucene walkaround
//        if (countFoldersByName(luceneIndexDirectoryName,f)>0) {
//			return (isSimDir(f)) // Safety file should not exist when it is deleted minus the simindex folder(s)
//		}
		// end
		return f.exists();
	}


	void createSimSafetyFile() {
		// add this for safety when deleting simulators -
		try {
			Io.stringToFile(simSafetyFile(), simId.toString());
		} catch (Exception e) {
			logger.severe("Cannot create safety file for simulator ${simId} - \n${ExceptionUtil.exception_details(e)}")
		}
	}

	void openMostRecentEvent(ActorType actor, TransactionType transaction) {
		openMostRecentEvent(actor.shortName, transaction.shortName)
	}

	void openMostRecentEvent(String actor, String transaction) {
		this.actor = ActorType.findActor(actor).shortName
		this.transaction = TransactionType.find(transaction).shortName
		transactionDir = transactionDirectory(actor, transaction)
		def trans = transactionDir.listFiles()
		String eventFullPath = (trans.size()) ? trans.sort().last() : null
		if (eventFullPath) {
			File eventFile = new File(eventFullPath)
			event = eventFile.name
		}
	}

	static SimDb open(SimDbEvent event) {
		assert event
		assert event.simId
		assert event.actor
		assert event.trans
		SimDb db = new SimDb(event.simId)
		db.transactionDir = db.transactionDirectory(event.actor, event.trans)
		db.event =  event.eventId
		db.actor = event.actor
		db.transaction = event.trans
		return db
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
					throw new IOException(String.format("Simulator ID contains bad character at position %d (%s)(%04x)", i, c, (int) c.charAt(0)));
				if (id.indexOf(c) != -1)
					throw new IOException(String.format("Simulator User (testSession) contains bad character at position %d", i));
			}
		}
		if (simId.testSession == null)
			throw new IOException("Simulator ID TestSession is null");
	}

	private File simSafetyFile() { return new File(simDir, "simId.txt"); }

	boolean isSim() {
		if (simDir == null) return false;
		return new File(simDir, "simId.txt").exists();
	}
	private static boolean isSimDir(File dir) { return new File(dir, "simId.txt").exists(); }


	Date getEventDate() {
		return eventDate;
	}


	static SimDb createMarker(SimId simId) {
		return new SimDb(simId, MARKER, MARKER, false)
	}

	/**
	 * Events returned most recent first
	 * If no marker then return all events.
	 * (I think this method assumes there is only one actor type and transaction type within the scope of a simulator because getAllEvents returns all events for all actors and all transactions.)
	 * @return
	 */
	List<SimDbEvent> getEventsSinceMarker() {
		getEventsSinceMarker(null, null)
	}

	List<SimDbEvent> getEventsSinceMarker(String actor, String tran) {
		List<SimDbEvent> events = getAllEvents(actor, tran)
		Map<String, SimDbEvent> eventMap = [:]
		events.each { SimDbEvent event -> eventMap[event.eventId] = event }
		def ordered = eventMap.keySet().sort().reverse()
		List<SimDbEvent> selected = []
		for (String event : ordered) {
			if (MARKER == eventMap[event].actor)
				break
			selected << eventMap[event]
		}
		return selected
	}

	/**
	 * Used by simproxy to get outbound sim half of proxy to have same event id (time stamp)
	 * @param otherSimDb
	 */
	void mirrorEvent(SimDb otherSimDb, String actor, String transaction) {
		this.actor = actor
		this.transaction = transaction
		transactionDir = transactionDirectory(actor, transaction)
		String event = otherSimDb.event
		eventDate = otherSimDb.getEventDate()
		File eventDir = mkEventDir(event)
		eventDir.mkdirs()
		Serialize.out(new File(eventDir, 'date.ser'), eventDate)
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
		String eventBase = Installation.asFilenameBase(date);
		return mkEventDir(eventBase)
	}

	private File mkEventDir(String eventBase) {
		int incr = 0;
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

	public String getEvent() { return event; }
	public void setEvent(String _event) { event = _event; }
	void setEvent(SimDbEvent event) {
		this.actor = event.actor
		this.transaction = event.trans
		configureTransactionDir()
		this.event = event.eventId
	}

	public File getEventDir() {
		return new File(transactionDir, event);
	}

	public void setClientIpAddess(String clientIpAddress) throws IOException {
		if (clientIpAddress != null) {
			Io.stringToFile(new File(getEventDir(), "ip.txt"), clientIpAddress);
		}
	}

	String getClientIpAddress() {
		File eventDir = getEventDir()
		if (eventDir?.isDirectory()) {
			return Io.stringFromFile(new File(eventDir, 'ip.txt'))
		}
		return null;
	}

//	SimDb(TransactionInstance ti) throws IOException, NoSimException, BadSimIdException {
//		this(getFullSimId(new SimId(testSession, ti.simId)))
//
//		this.actor = ti.actorType.getShortName();
//		this.transaction = ti.trans;
//
//		if (actor != null && transaction != null) {
//			configureTransactionDir()
//		}
//		event = ti.messageId;
//	}

	private void configureTransactionDir() {
		String transdir = new File(new File(simDir, actor), transaction).path;
		transactionDir = new File(transdir);
		transactionDir.mkdirs();
		if (!transactionDir.isDirectory())
			throw new IOException("Cannot create content in Simulator database, creation of " + transactionDir + " failed");
	}

	// actor, transaction, and event must be filled in
	private Date retrieveEventDate() throws IOException, ClassNotFoundException {
		if (transactionDir == null || event == null) return null;
		File eventDir = new File(transactionDir, event);
		eventDate = (Date) Serialize.in(new File(eventDir, "date.ser"));
		return eventDate;
	}

	/**
	 * Delete simulator
	 */
	void delete() {
		if (isSim()) {
			if (simId != null) {
                /*
				File indexFile = getIndexFile(simId);
				if (indexFile != null && indexFile.exists()) {
					stopLuceneIndex(indexFile)
//					On Windows, there a problem with deleting Lucene indexes.
//					See https://lucene.apache.org/core/7_0_1/core/org/apache/lucene/store/FSDirectory.htm
//					deleteLuceneIndex(indexFile).
//					Walkaround is to use a new index folder on every delete and pickup the most recent folder on new-sim index.
//                    delete(simDir)
					Io.delete(simDir, luceneIndexDirectoryName)
					if (isSimDir(simDir)) { // This should not happen
						throw new RuntimeException("delete failed for " + simDir)
					}
					int folderCt = countFoldersByName(luceneIndexDirectoryName, simDir)
					File newIndexDir = new File(getSimBase(simId), luceneIndexDirectoryName + String.format("%03d", (folderCt + 1)))
					if (!newIndexDir.exists()) {
						newIndexDir.mkdirs()
					} else {
						delete(newIndexDir)
						newIndexDir.mkdirs()
					}
				} else {
				*/
					delete(simDir)
//				}
			}
		}
	}

	static private int countFoldersByName(String prefixFn, File path) {
		String[] names = path.list(new PrefixFileFilter(prefixFn))
		if (names!=null)
			return names.length
		else
			return 0
	}

	/*
	private void stopLuceneIndex(File indexFile) {
		if (indexFile != null && indexFile.exists()) {
            if (SimIndex.getIndexMap().containsKey(simId)) {
                logger.info("SimIndex map contains " + simId)
				SiTypeWrapper typeWrapper = SimIndex.getIndexMap().get(simId)
				if (typeWrapper.getIndexer() instanceof Closeable) {
					logger.info("SimIndex map object is an instance of Closeable")
					typeWrapper.getIndexer().close()
					SimIndex.getIndexMap().remove(simId)
				} else {
					logger.info("SimIndex map object is Not an instance of Closeable")
				}
			}
		}
	}
	*/

    /*
	private void deleteLuceneIndex(File indexFile) {
	 if (indexFile != null && indexFile.exists()) {
		FSDirectory dir = null
		try {
				dir = FSDirectory.open(indexFile.toPath())
				for (String s : dir.listAll(indexFile.toPath())) {
					dir.deleteFile(s)
				}
//			 Lucene v7.0.1
//                if (dir.checkPendingDeletions()) {
//					dir.deletePendingFiles()
//				}

		} catch (Exception ex) {
			logger.severe(ex.toString())
		} finally {
			if (dir)
				dir.close()
		}
	 }
	}
*/

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
//			logger.severe("Extended Property expiration of class " + controllingClass + " is not defined");
			dayOffset = "1";
		}
		newExpiration.add(Calendar.DAY_OF_MONTH, Integer.parseInt(dayOffset));
		return newExpiration.getTime();
	}

	public void deleteAllSims(TestSession testSession) throws IOException, NoSimException {
		List<SimId> allSimIds = getAllSimIds(testSession);
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

	static private SimId internalSimIdBuilder(File simDefDir, TestSession testSession) {
		SimId simId = SimIdFactory.simIdBuilder(simDefDir.name)//new SimId(testSession, simDefDir.name)
//		if (isFSim(simId)) simId.forFhir()
		try {
			simId.actorType = new SimDb(simId).getSimulatorType()
		} catch (Exception e) {

		}
		simId
	}

	static SimId simIdBuilder(String rawId) {
		SimIdFactory.simIdBuilder(rawId)
	}

	// is this sim valid - does it have the necessary parts
	static boolean isValid(SimId simId) {
		File d = getSimDbFile(simId)
		File f = new File(d, simId.toString())
		if (! new File(f, 'simId.txt').exists())
			return false
//		if (! new File(f, simTypeFilename).exists())
//			return false
		if (! new File(f, 'simctl.json').exists())
			return false
		return true;
	}

	static void scanAllSims() {
		List<TestSession> testSessions = Installation.instance().getTestSessions()
		testSessions.each { TestSession testSession ->
			List<SimId> simIds = getAllSimIds(testSession)
			simIds.each { SimId simId ->
				if (!isValid(simId))
					throw new ToolkitRuntimeException("Invalid SIM ${simId} found")
			}
		}
	}

	static List<SimId> getAllSimIds(TestSession testSession) throws BadSimIdException {

		List soapSimIds = getSimDbFile(testSession).listFiles().findAll { File file ->
			isSimDir(file)
		}.collect { File dir ->
			internalSimIdBuilder (dir, testSession)
		}

		List fhirSimIds = getFSimDbFile(testSession).listFiles().findAll { File file ->
			isSimDir(file)
		}.collect { File dir ->
			internalSimIdBuilder(dir, testSession)
		}

		Set defaultSims = []
		if (testSession != TestSession.DEFAULT_TEST_SESSION) {
			defaultSims = getSimDbFile(TestSession.DEFAULT_TEST_SESSION).listFiles().findAll { File file ->
				isSimDir(file)
			}.collect { File dir ->
				internalSimIdBuilder (dir, TestSession.DEFAULT_TEST_SESSION)
			} as Set
		}

		def ids = (soapSimIds + fhirSimIds + defaultSims) as Set<SimId>
		return ids as List<SimId>
	}

	/**
	 * should always use SimId - carries more information
	 * @return
	 */
	@Obsolete
	static List<String> getAllSimNames(TestSession testSession) {
		getAllSimIds(testSession).collect { it.toString()}
	}

	static List<SimId> getSimIdsForUser(TestSession user) throws BadSimIdException {
		return getAllSimIds(user)
	}

	/**
	 * Get a simulator.
	 * @return simulator if it exists or null
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public SimulatorConfig getSimulator(SimId simId) throws SimDoesNotExistException {
		SimulatorConfig config = null;
		boolean okIfNotExist = true;
		int retry = 3;
		// Sometimes loadSimulator returns Null even though there is valid simulator
		while (config == null && retry-->0) {
			try {
				config = GenericSimulatorFactory.loadSimulator(simId, okIfNotExist);
			} catch (Exception ex) {
				Thread.sleep(100);
				logger.info("LoadSimulator retrying attempt..." + retry);
			}
		}

		if (!okIfNotExist && retry==0 && config==null)
			throw new SimDoesNotExistException("Null config for " + simId.toString() + " even after retry attempts.");

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
				logger.warning(ex.toString());
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

    /*
	public void addhl7v2Msg(String hl7msg, String msh9, String dateDir, boolean inboundMsg) {
		if (hl7msg == null || hl7msg.isEmpty()) hl7msg="null or empty hl7 msg";
		String xmlmsg = hl7v2ToXml(hl7msg);
		if (msh9 == null || msh9.isEmpty()) msh9 = "none";
		String hl7fn = inboundMsg ? "Request.txt" : "Response.txt";
		String xmlfn = inboundMsg ? "Request.xml" : "Response.xml";
		File dir = Paths.get(simDir.getPath(),"hl7v2", msh9, dateDir).toFile();
		dir.mkdirs();
		Files.write(dir.toPath().resolve(hl7fn), hl7msg.getBytes("UTF-8"));
		Files.write(dir.toPath().resolve(xmlfn), xmlmsg.getBytes("UTF-8"));
	}
     */

	public boolean deletePatientIds(List<Pid> toDelete) {
		return pidDb.deletePatientIds(toDelete);
	}

	public boolean patientIdExists(Pid pid) throws IOException {
		return pidDb.patientIdExists(pid);
	}

	public ActorType getSimulatorActorType() {
		SimulatorConfig config = AbstractActorFactory.getSimConfig(simId)
		ActorType.findActor(config.actorType)
//		File typeFile = new File(simDir, simTypeFilename);
//		String name = null;
//		try {
//			name = Io.stringFromFile(typeFile).trim();
//		} catch (IOException e) {
//			return null;
//		}
//		return ActorType.findActor(name);
	}

	public List<SimId> getSimulatorIdsforActorType(ActorType actorType, TestSession testSession) throws IOException, NoSimException {
		List<SimId> allSimIds = getAllSimIds(testSession);
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
		SimulatorConfig config = AbstractActorFactory.getSimConfig(simId)
		config.actorType
//		File simType = new File(simDir, simTypeFilename);
//		return Io.stringFromFile(simType).trim();
	}

//	public void setSimulatorType(String type) throws IOException {
//		File simType = new File(simDir, simTypeFilename);
//		Io.stringToFile(simType, type);
//	}

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

					TransactionInstance t = buildTransactionInstance(actor, inst, name)

					//logger.fine("Found " + t);
					if (!t.isPif)
						transList.add(t);
				}
			}
		}

//		Collections.sort(transList, new ReverseTransactionInstanceComparator());

		transList = transList.sort { TransactionInstance ti -> ti.messageId }.reverse()

		event = event_save;
		transactionDir = transDir_save;
//		logger.fine("returning " + transList);
		return transList;
	}

	private File getActorDir(String actor) {
		new File(simDir, actor)
	}

	TransactionInstance buildTransactionInstance(String actor, String messageId, String trans) {
		buildTransactionInstance(getActorDir(actor), new File(messageId) , trans)
	}

	// This nesses with transactionDir which must be saved before and restored afterwards
	TransactionInstance buildTransactionInstance(File actor, File inst, String name) {
		TransactionInstance t = new TransactionInstance();
		boolean isPif = false
		if (name == TransactionType.PIF.shortName) {
			isPif = true
			t.isPif = true
		}
		t.simId = simId.toString();
		t.actorType = ActorType.findActor(actor.getName());
		t.messageId = inst.getName();
		t.trans = name;
		transactionDir = new File(actor, name);
		//logger.fine("transaction dir is " + transactionDir);
		event = t.messageId;
		Date date = null;
		try {
			date = retrieveEventDate();
		} catch (IOException e) {
		} catch (ClassNotFoundException e) {
		}
//					if (date == null) continue;  // only interested in transactions that have dates
		t.labelInterpretedAsDate = (date == null) ? event?:"" : date.toString();
		t.nameInterpretedAsTransactionType = TransactionType.find(t.trans);

		String ipAddr = null;
		File ipAddrFile = new File(inst, "ip.txt");
		if (isPif) {

		} else {
			try {
				ipAddr = Io.stringFromFile(ipAddrFile);
				if (ipAddr != null && !ipAddr.equals("")) {
					t.ipAddress = ipAddr;
				}
			} catch (IOException e) {
			}
		}
		t
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
		assert simDir
		assert actor
		assert transaction
		assert event
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

	public void putResponseBody(byte[] content) throws IOException {
		Io.bytesToFile(getResponseBodyFile(), content);
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
		assert filenamebase
		return new File(getDBFilePrefix(filenamebase), REQUEST_HEADER_FILE);
	}

	File getRequestURIFile(String filenamebase) {
		assert filenamebase
		return new File(getDBFilePrefix(filenamebase), REQUEST_URI_FILE)
	}

	private File getRequestMsgBodyFile(String filenamebase) {
		assert filenamebase
		return new File(getDBFilePrefix(filenamebase), REQUEST_BODY_BIN_FILE);
	}

	private File getAlternateRequestMsgBodyFile(String filenamebase) {
		assert filenamebase
		return new File(getDBFilePrefix(filenamebase), REQUEST_BODY_TXT_FILE);
	}

	private File getResponseMsgHdrFile(String filenamebase) {
		assert filenamebase
		return new File(getDBFilePrefix(filenamebase), RESPONSE_HEADER_FILE);
	}

	@Obsolete
	private File getResponseMsgBodyFile(String filenamebase) {
		assert filenamebase
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

	void logErrorRecorder(GwtErrorRecorder er) {
		Io.stringToFile(getLogFile(), er.toString())
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

	@Obsolete
	void rename(String fileNameBase, String newFileNameBase) throws IOException {

		File from = getDBFilePrefix(fileNameBase);
		File to = getDBFilePrefix(newFileNameBase);
		boolean stat = from.renameTo(to);

		if (!stat)
			throw new IOException("Rename failed");

	}

	// name of sim directory is the name we want
	// make sure internals are up to date with it
	void updateSimConfiguration() {
		createSimSafetyFile()  // actually update
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

	List<SimDbEvent> getAllEvents() {
		getAllEvents(null, null);
	}

	/**
	 *
	 * @param actor Optional.
	 * @param tran Optional.
	 * @return
	 */
	List<SimDbEvent> getAllEvents(String actor, String tran) {
		List<SimDbEvent> eventDirs = []
		for (File actorDir : simDir.listFiles()) {
			if (!actorDir.isDirectory()) continue
			if (actor?actorDir.getName().equals(actor):true) {
				for (File transDir : actorDir.listFiles()) {
					if (!transDir.isDirectory()) continue
                    if ((tran?transDir.getName().equals(tran):true)) {
						for (File eventDir : transDir.listFiles()) {
							eventDirs << new SimDbEvent(simId, actorDir.name, transDir.name, eventDir.name)
						}
					}
				}
			}
		}
		return eventDirs
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
		assert event
		return getRequestURIFile(event)
	}

	public File getRequestHeaderFile() {
		assert event
		return getRequestMsgHdrFile(event);
	}

	public File getRequestBodyFile() {
		assert event
		return getRequestMsgBodyFile(event);
	}

	private File getAlternateRequestBodyFile() {
		assert event
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

	void putResponseHeaderFile(byte[] bytes) {
		if (!getResponseHdrFile().exists())
			Io.bytesToFile(getResponseHdrFile(), bytes)
	}

	void putResponseHeader(String str) {
		getResponseHdrFile().text = str
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

	static File getResDbFile(TestSession testSession) {
		return Installation.instance().fhirSimDbFile(testSession)
	}


	/**
	 * Get location of Lucene index for this simulator
	 * @param simId
	 * @return
	 */
	/*
	static File getIndexFile(SimId simId) {
        // Lucene walkaround
		int count = countFoldersByName(luceneIndexDirectoryName, getSimBase(simId))
		if (count>0) {
			File[] files = getSimBase(simId).listFiles((FilenameFilter)new PrefixFileFilter(luceneIndexDirectoryName))
			if (files!=null && files.length>0) {
				Arrays.sort(files, NameFileComparator.NAME_REVERSE)
				return files[0]
			}
		}
		return new File(getSimBase(simId), luceneIndexDirectoryName)
	}

	 */

	/**
	 * Base location of FHIR simulator
	 * @param simId - which simulator
	 * @return
	 */
	static File getSimBase(SimId simId) {
		assert simId?.testSession?.value
		return new File(getResDbFile(simId.testSession), simId.toString())
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
		assert simid?.testSession?.value
		return mkfSimi(getResDbFile(simid.testSession), simid, BASE_TYPE, true)
	}

	private static SimDb mkfSimi(File dbRoot, SimId simid, String actor, boolean openToLastEvent) throws IOException, NoSimException {
		simid.forFhir()
		validateSimId(simid);
		if (!dbRoot.exists())
			dbRoot.mkdirs();
		if (!dbRoot.canWrite() || !dbRoot.isDirectory())
			throw new IOException("Resource Simulator database location, " + dbRoot.toString() + " is not a directory or cannot be written to");

		File simActorDir = new File(dbRoot.getAbsolutePath() + File.separatorChar + simid + File.separatorChar + actor);
		simActorDir.mkdirs();
		if (!simActorDir.exists()) {
			logger.severe("Fhir Simulator " + simid + ", " + actor + " cannot be created");
			throw new IOException("Fhir Simulator " + simid + ", " + actor + " cannot be created");
		}

		SimDb db = new SimDb(simid, BASE_TYPE, null, openToLastEvent);
//		db.setSimulatorType(actor);
		return db;
	}

	/**
	 * Converts hl7 V2 message to XML
	 * @param inMsg hl7 v2 message.
	 * @return XML equivalent. On error, returns error message and original hl7 msg.
	 */
    /*
	public String hl7v2ToXml(String inMsg) {
		try {
			Reader reader = new StringReader(inMsg);
			SAXTransformerFactory tf = (SAXTransformerFactory) TransformerFactory.newInstance();
			TransformerHandler th = tf.newTransformerHandler();
			th.getTransformer().setOutputProperty(OutputKeys.INDENT, "yes");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			th.setResult(new StreamResult(baos));
			HL7Parser hl7Parser = new HL7Parser(th);
			hl7Parser.setIncludeNamespaceDeclaration(false);
			hl7Parser.parse(reader);
			return baos.toString("UTF-8");
		} catch (Exception e) {
			return "Error converting to XML: " + e.getMessage() +
				System.getProperty("line.separator") + inMsg;
		}
	}
	*
     */
	void setActor(String actor) {
		this.actor = actor
	}

	void setTransaction(String transaction) {
		this.transaction = transaction
	}

	TestSession getTestSession() {
		if (!testSession)
			testSession = simId.testSession
		return testSession
	}

	@Override
	String toString() {
		simId.toString()
	}

}
