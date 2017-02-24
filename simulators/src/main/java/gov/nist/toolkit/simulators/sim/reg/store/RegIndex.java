package gov.nist.toolkit.simulators.sim.reg.store;

import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.actorfactory.client.NoSimException;
import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.actorfactory.client.SimulatorStats;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.valsupport.registry.RegistryValidationInterface;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RegIndex implements RegistryValidationInterface, Serializable {
	private static Logger logger = Logger.getLogger(RegIndex.class);

	private static final long serialVersionUID = 1L;
	public MetadataCollection mc;
	private String filename;
	public Calendar cacheExpires;
	transient private SimDb db;
	private SimId simId;

	public RegIndex() {}

	public RegIndex(String filename, SimId simId) {
		this.filename = filename;
		this.simId = simId;
		try {
//		logger.debug("Restore Registry Index");
			logger.debug("Attempting to Restore Registry Index");
			restore();
			mc.regIndex = this;
			mc.setDirty(false);
		} catch (Exception e) {
			// no existing database - initialize instead
			logger.debug("No existing - creating new");
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

	public enum AssocType { UNKNOWN, HASMEMBER, RPLC, RPLC_XFRM, XFRM, APND };

	static public AssocType getAssocType(Metadata m, OMElement ele) {
		String typ = Metadata.getAssocType(ele);
		return getAssocType(typ);
	}

	public static AssocType getAssocType(String typ) {
		if (typ == null)
			return AssocType.UNKNOWN;
		if (typ.endsWith("HasMember"))
			return AssocType.HASMEMBER;
		if (typ.endsWith("RPLC"))
			return AssocType.RPLC;
		if (typ.endsWith("RPLC_XFRM"))
			return AssocType.RPLC_XFRM;
		if (typ.endsWith("XFRM"))
			return AssocType.XFRM;
		if (typ.endsWith("APND"))
			return AssocType.APND;
		return AssocType.UNKNOWN;
	}
	
	 public static String getAssocString(AssocType type) {
		switch (type) {
		case HASMEMBER: return "HasMember";
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

		stats.put(SimulatorStats.DOCUMENT_ENTRY_COUNT, mc.docEntryCollection.size());
		stats.put(SimulatorStats.SUBMISSION_SET_COUNT, mc.subSetCollection.size());
		stats.put(SimulatorStats.ASSOCIATION_COUNT, mc.assocCollection.size());
		stats.put(SimulatorStats.FOLDER_COUNT, mc.folCollection.size());

		SimDb db = new SimDb(simId);
		stats.put(SimulatorStats.PATIENT_ID_COUNT, db.getAllPatientIds().size());

		return stats;
	}
}
