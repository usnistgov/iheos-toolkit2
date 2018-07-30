package gov.nist.toolkit.fhir.simulators.sim.reg.store;

import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.simcommon.client.NoSimException;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.SimulatorStats;
import gov.nist.toolkit.simcommon.server.DbObjectType;
import gov.nist.toolkit.simcommon.server.SimDb;
import gov.nist.toolkit.valsupport.registry.RegistryValidationInterface;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RegIndex implements RegistryValidationInterface, Serializable {
	private static Logger logger = Logger.getLogger(RegIndex.class);

	private static final long serialVersionUID = 1L;
	public MetadataCollection mc;
	private String filename;  // of the index file
	public Calendar cacheExpires;
	transient private SimDb db;
	private SimId simId;

	// path is created relative to index file parent folder
	File installInternalPath(Ro ro) {
		Path indexPath = new File(filename).getParentFile().toPath();
		File absolute = getSimDb().getObjectFile(DbObjectType.REGISTRY, ro.id);
		ro.pathToMetadata = indexPath.relativize(absolute.toPath()).toString();
//		ro.pathIsRelative = true;
//		logger.info("RegStore: " + absolute + ":" + ro.pathToMetadata);
		return absolute;
	}

	// resolve relative to index file parent
	public Path getAbsolutePathForObject(Ro ro) {
		if (!(ro.isPathIsRelative()))
			return new File(ro.pathToMetadata).toPath();
		Path indexPath = new File(filename).getParentFile().toPath();
		Path path = indexPath.resolve(ro.pathToMetadata);
//		logger.info("RegRead: " + path);
		return path;
	}

	public RegIndex() {}

	public RegIndex(String filename, SimId simId) {
		this.filename = filename;
		this.simId = simId;
		try {
			logger.debug("Restore Registry Index for " + simId.toString() + " from " + filename);
			restore();
			mc.regIndex = this;
			mc.setDirty(false);
		} catch (Exception e) {
			logger.debug("Creating new Registry Index for " + simId.toString() + " at " + filename);
			mc = new MetadataCollection();
			mc.init();
			mc.regIndex = this;
			mc.setDirty(false);
			mc.clearAllCollections();
		}
	}

	public RegIndex(MetadataCollection metadataCollection) {
		mc = metadataCollection;
	}
	
	public void setSimDb(SimDb db) {
		this.db = db;
	}
	
	public SimDb getSimDb() {
		return db;
	}
	
	static public List<StatusValue> docEntryLegalStatusValues = new ArrayList<StatusValue>() {{ add(StatusValue.APPROVED); add(StatusValue.DEPRECATED); }};

	public void setExpiration(Calendar expires) {
		this.cacheExpires = expires;
	}

	static public StatusValue getStatusValue(String stat) {
		if (stat == null)
			return StatusValue.UNKNOWN;
		if (stat.endsWith("Approved"))
			return StatusValue.APPROVED;
		if (stat.endsWith("Deprecated"))
			return StatusValue.DEPRECATED;
		return StatusValue.UNKNOWN;
	}

	public static  String getStatusString(StatusValue status) {
		switch(status) {
		case APPROVED: 
			return "urn:oasis:names:tc:ebxml-regrep:StatusType:Approved";
		case DEPRECATED:
			return "urn:oasis:names:tc:ebxml-regrep:StatusType:Deprecated";
		case UNKNOWN:
			return "";
		default:
			return "";
		}
	}

	public List<StatusValue> translateStatusValues(List<String> strings) {
		List<StatusValue> values = new ArrayList<StatusValue>();

		for (String s : strings) 
			values.add(getStatusValue(s));

		return values;
	}

	public enum AssocType { UNKNOWN, HasMember, RPLC, RPLC_XFRM, XFRM, APND, SIGNS };

	public enum RelationshipAssocType { RPLC, RPLC_XFRM, XFRM, APND, SIGNS}

	public static boolean isRelationshipAssoc(String value) {
		try {
			RelationshipAssocType.valueOf(value);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	static public AssocType getAssocType(Metadata m, OMElement ele) {
		String typ = Metadata.getAssocType(ele);
		return getAssocType(typ);
	}

	public static AssocType getAssocType(String typ) {
		if (typ == null)
			return AssocType.UNKNOWN;
		if (typ.endsWith("HasMember"))
			return AssocType.HasMember;
		if (typ.endsWith("RPLC"))
			return AssocType.RPLC;
		if (typ.endsWith("RPLC_XFRM"))
			return AssocType.RPLC_XFRM;
		if (typ.endsWith("XFRM"))
			return AssocType.XFRM;
		if (typ.endsWith("APND"))
			return AssocType.APND;
		if (typ.endsWith("SIGNS"))
			return AssocType.SIGNS;
		return AssocType.UNKNOWN;
	}
	
	 public static String getAssocString(AssocType type) {
		switch (type) {
		case HasMember: return "HasMember";
		default: return type.toString();
		}
	}

	private static void saveRegistry(MetadataCollection mc, String filename) throws IOException {
		logger.debug("Save Registry Index");
		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		fos = new FileOutputStream(filename);
		out = new ObjectOutputStream(fos);
		out.writeObject(mc);
		out.close();
	}

	// This must be called from a synchronize block
	private static MetadataCollection restoreRegistry(String filename) throws IOException, ClassNotFoundException {
		FileInputStream fis = null;
		ObjectInputStream in = null;
		MetadataCollection mc;
		try {
			fis = new FileInputStream(filename);
			in = new ObjectInputStream(fis);
			mc = (MetadataCollection)in.readObject();
		} finally {
			if (in != null)
				in.close();
			if (fis!=null)
				fis.close();
		}
		return mc;
	}

// caller takes responsiblity for sync, must be on this
	public void save() throws IOException {
			saveRegistry(mc, filename);
			mc.setDirty(false);
	}

	private void restore() throws IOException, ClassNotFoundException {
		synchronized(this) {
			mc = restoreRegistry(filename);
		}
	}

	public MetadataCollection getMetadataCollection() {
		mc.regIndex = this;
		return mc;
	}

	public boolean isDocumentEntry(String uuid) {
		return mc.docEntryCollection.hasObject(uuid);
	}

	public boolean isFolder(String uuid) {
		return mc.folCollection.hasObject(uuid);
	}

	public boolean isSubmissionSet(String uuid) {
		return mc.subSetCollection.hasObject(uuid);
	}

	public SimulatorStats getSimulatorStats() throws IOException, NoSimException {
		SimulatorStats stats = new SimulatorStats();
		stats.actorType = ActorType.REGISTRY;
		stats.simId = simId;

		stats.put(SimulatorStats.DOCUMENT_ENTRY_COUNT, (mc!=null&&mc.docEntryCollection!=null)?mc.docEntryCollection.size():0);
		stats.put(SimulatorStats.SUBMISSION_SET_COUNT, (mc!=null&&mc.subSetCollection!=null)?mc.subSetCollection.size():0);
		stats.put(SimulatorStats.ASSOCIATION_COUNT, (mc!=null&&mc.assocCollection!=null)?mc.assocCollection.size():0);
		stats.put(SimulatorStats.FOLDER_COUNT,  (mc!=null&&mc.folCollection!=null)?mc.folCollection.size():0);

		SimDb db = new SimDb(simId);
		stats.put(SimulatorStats.PATIENT_ID_COUNT, (db!=null&&db.getAllPatientIds()!=null)?db.getAllPatientIds().size():0);

		return stats;
	}
}
