package gov.nist.toolkit.simulators.sim.reg.store;

import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.valsupport.registry.RegistryValidationInterface;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

public class RegIndex implements RegistryValidationInterface, Serializable {
	static Logger logger = Logger.getLogger(RegIndex.class);

	private static final long serialVersionUID = 1L;
	public MetadataCollection mc;
	String filename;
	public Calendar cacheExpires;
	transient SimDb db;
	
	public RegIndex() {}

	public RegIndex(String filename) {
		this.filename = filename;
		try {
			restore();
			mc.regIndex = this;
			mc.dirty = false;
		} catch (Exception e) {
			// no existing database - initialize instead
			mc = new MetadataCollection();
			mc.init();
			mc.regIndex = this;
			mc.dirty = false;
			mc.allCollections = null;
		}
	}
	
	public void setSimDb(SimDb db) {
		this.db = db;
	}
	
	public SimDb getSimDb() {
		return db;
	}

	static public enum StatusValue { UNKNOWN, APPROVED, DEPRECATED };
	
	
	
	static public List<StatusValue> docEntryLegalStatusValues = new ArrayList<StatusValue>() {{ add(StatusValue.APPROVED); add(StatusValue.DEPRECATED); }};
	
	public class OldValueNewValueStatus {
		StatusValue o;
		StatusValue n;
		String id;
		Ro ro;
		
		
		public OldValueNewValueStatus(StatusValue oldValue, StatusValue newValue, String id) {
			o = oldValue;
			n = newValue;
			this.id = id;
		}
	}

	static public StatusValue getStatusValue(Metadata m, OMElement ele) {
		String stat = m.getStatus(ele);
		return getStatusValue(stat);
	}

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

	static public String getStatusString(StatusValue status) {
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

	public String statsToString() {
		return mc.statsToString();
	}

	class PidDb {
		List<String> knownPids;
	}

	public enum AssocType { UNKNOWN, HASMEMBER, RPLC, RPLC_XFRM, XFRM, APND };

	static public AssocType getAssocType(Metadata m, OMElement ele) {
		String typ = m.getAssocType(ele);
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

	static void saveRegistry(MetadataCollection mc, String filename) throws IOException {
		logger.debug("Save Registry Index");
		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		fos = new FileOutputStream(filename);
		out = new ObjectOutputStream(fos);
		out.writeObject(mc);
		out.close();
	}

	static MetadataCollection restoreRegistry(String filename) throws IOException, ClassNotFoundException {
		logger.debug("Restore Registry Index");
		FileInputStream fis = null;
		ObjectInputStream in = null;
		MetadataCollection mc;
		fis = new FileInputStream(filename);
		in = new ObjectInputStream(fis);
		mc = (MetadataCollection)in.readObject();
		in.close();
		return mc;
	}

// caller takes responsiblity for sync, must be on this
	public void save() throws IOException {
//		if (!mc.dirty)
//			return;
//		synchronized(this) {
			RegIndex.saveRegistry(mc, filename);
			mc.dirty = false;
//		}
	}

	public void restore() throws IOException, ClassNotFoundException {
		synchronized(this) {
			mc = RegIndex.restoreRegistry(filename);
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


}
