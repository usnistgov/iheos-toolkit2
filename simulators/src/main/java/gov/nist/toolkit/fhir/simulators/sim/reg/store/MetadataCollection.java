package gov.nist.toolkit.fhir.simulators.sim.reg.store;

import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.fhir.simulators.sim.reg.store.RegIndex.AssocType;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymetadata.MetadataParser;
import gov.nist.toolkit.utilities.id.UuidAllocator;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.utilities.xml.OMFormatter;
import gov.nist.toolkit.valregmsg.registry.SQStatusTerm;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.registry.RegistryValidationInterface;
import gov.nist.toolkit.xdsexception.client.MetadataException;
import gov.nist.toolkit.xdsexception.client.MetadataValidationException;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class MetadataCollection implements RegistryValidationInterface, Serializable {
	private static final long serialVersionUID = 1L;

	public DocEntryCollection docEntryCollection;
	public AssocCollection assocCollection;
	public SubSetCollection subSetCollection;
	public FolCollection folCollection;

	// collection of collections
	transient private List<RegObCollection> allCollections = null;

	public transient FolCollection updatedFolCollection;
	transient private boolean dirty;
	transient public RegIndex regIndex;
	transient public ValidationContext vc;

	// To maintain a delta ...
	private transient MetadataCollection parent = null;
	private transient List<OldValueNewValueStatus> statusChanges = null;

	public MetadataCollection() {
		init();
		buildAllCollections();
	}

	public void setDeleting(List<String> ids) {
		docEntryCollection.setDeleting(ids);
		assocCollection.setDeleting(ids);
		subSetCollection.setDeleting(ids);
		folCollection.setDeleting(ids);
	}

	private Logger logger() { return Logger.getLogger(MetadataCollection.class); }

	// create a delta for this collection
	public MetadataCollection mkDelta() {
		MetadataCollection delta = new MetadataCollection();

		delta.init();
		delta.parent = this;
		delta.regIndex = regIndex;
		delta.docEntryCollection.parent = docEntryCollection;
		delta.folCollection     .parent = folCollection;
		delta.assocCollection   .parent = assocCollection;
		delta.subSetCollection  .parent = subSetCollection;

		delta.updatedFolCollection = new FolCollection();
		delta.updatedFolCollection.init();

		delta.statusChanges = new ArrayList<OldValueNewValueStatus>();

		return delta;
	}

	public String getStats() { return getStats(""); }

	private String getStats(String prefix) {
		StringBuilder buf = new StringBuilder();

		buf
				.append(prefix).append(subSetCollection.statsToString())
				.append('\n').append(prefix).append(docEntryCollection.statsToString())
				.append('\n').append(prefix).append(folCollection.statsToString())
				.append('\n').append(prefix).append(subSetCollection.statsToString())
				.append('\n');
		return buf.toString();
	}

	public String getIdStats(String prefix) {
		StringBuilder buf = new StringBuilder();

		buf
				.append(prefix).append(subSetCollection.getIds())
				.append('\n').append(prefix).append(docEntryCollection.getIds())
				.append('\n').append(prefix).append(folCollection.getIds())
				.append('\n').append(prefix).append(subSetCollection.getIds())
				.append('\n');
		return buf.toString();
	}

	public void changeAvailabilityStatus(String id, StatusValue oldValue, StatusValue newValue) {
		OldValueNewValueStatus ons = new OldValueNewValueStatus(oldValue, newValue, id);
		statusChanges.add(ons);
	}

	// A delta has been created during the operation of a Register transaction
	// Merge the delta into the parent record
	// Caller takes responsibility for locking
	boolean mergeDelta(ValidationContext vc, ErrorRecorder er) {
		if (parent == null)
			return false;

		// do the parts that can fail first
		// first loop checks for problems, second makes changes

		for (OldValueNewValueStatus ons : statusChanges) {
			StatusValue oldVal = ons.o;
			String id = ons.id;

			Ro obj = getObjectById(id);
			if (obj == null) {
				er.err(Code.XDSRegistryError, "mergeDelta: cannot find model " + id, this, null);
				return false;
			}

			if (obj.getAvailabilityStatus() != oldVal && !vc.isRMU) {
				er.err(Code.XDSRegistryError, "mergeDelta: old status has changed", this, null);
				return false;
			}

			ons.ro = obj;

		}

		for (OldValueNewValueStatus ons : statusChanges) {
			StatusValue newVal = ons.n;

			ons.ro.setAvailabilityStatus(newVal);
		}

		for (Fol f  : updatedFolCollection.getAll()) {
			String fid = f.getId();

			for (int i=0; i<parent.folCollection.getAll().size(); i++) {
				Fol origFol = parent.folCollection.getAllForUpdate().get(i);

				if (fid.equals(origFol.getId())) {
					parent.folCollection.getAllForUpdate().set(i, f);
					break;
				}
			}
		}


		parent.docEntryCollection.getAllForUpdate().addAll(docEntryCollection.getAllForUpdate());

		parent.assocCollection.getAllForUpdate().addAll(assocCollection.getAllForUpdate());

		parent.folCollection.getAllForUpdate().addAll(folCollection.getAllForUpdate());

		parent.subSetCollection.getAllForUpdate().addAll(subSetCollection.getAllForUpdate());

		return true;
	}

	void labelFolderUpdated(Fol f, String lastUpdateTime) {
		Fol nf = f.clone();
		nf.lastUpdateTime = lastUpdateTime;
		updatedFolCollection.getAllForUpdate().add(nf);
	}

	public void init() {
		docEntryCollection = new DocEntryCollection();
		docEntryCollection.init();

		assocCollection = new AssocCollection();
		assocCollection.init();

		subSetCollection = new SubSetCollection();
		subSetCollection.init();

		folCollection = new FolCollection();
		folCollection.init();

		dirty = false;
	}

	public void mkDirty() {
		dirty = true;
	}

	private void buildAllCollections() {
//		allCollections = null;
		if (allCollections == null)
			allCollections = new ArrayList<>(); // GWT needs explicit type in ArrayList<>
		allCollections.clear();
		allCollections.add(docEntryCollection);
		allCollections.add(assocCollection);
		allCollections.add(subSetCollection);
		allCollections.add(folCollection);

//		logger().debug("Current metadata index\n" + getStats("   "));
//		logger().debug("...\n" + getIdStats("    "));
	}

	public String deleteRo(String id) {

		List<String> deleting = new ArrayList<>(docEntryCollection.idsBeingDeleted());
		setDeleting(new ArrayList<String>());

		buildAllCollections();

		for (RegObCollection roc : allCollections) {
			Ro ro = roc.getRo(id);
			if (ro == null)
				continue;
			String uid = null;
			if (ro instanceof DocEntry) {
				uid = ro.uid;
			}

			System.out.println("Delete " + id);

			synchronized(regIndex) {
				// delete entry in registry index
				if(roc.delete(id))
					setDirty(true);

				// delete file containing xml
				File xml = ro.getFile();
				xml.delete();


			}
			setDeleting(deleting);
			return uid;
		}
		setDeleting(deleting);
		return null;
	}

	// purge objects from the index that are no longer present in files behind the index
	public void purge() {
		buildAllCollections();

		for (RegObCollection roc : allCollections) {
			List<?>	ros = roc.getAllRo();
			for (Object o : ros) {
				if (! (o instanceof Ro))
					continue;
				Ro ro = (Ro) o;
				if (!ro.metadataExists()) {
					synchronized(regIndex) {
						roc.delete(ro.getId());
					}
				}
			}
		}
	}

	List<Fol> getFoldersContaining(String id) {
		List<Fol> fols = new ArrayList<Fol>();

		List<Assoc> hasmembers = assocCollection.getBySourceDestAndType(null, id, AssocType.HasMember);

		for (Assoc a : hasmembers) {
			if (isFolder(a.from))
				fols.add(folCollection.getById(a.from));
		}

		return fols;
	}

	public List<DocEntry> getDocEntriesInFolder(Fol fol) {
		List<DocEntry> des = new ArrayList<>();

		List<Assoc> hasmembers = assocCollection.getBySourceDestAndType(fol.id, null, AssocType.HasMember);
		for (Assoc a : hasmembers) {
			des.add(docEntryCollection.getById(a.to));
		}

		return des;
	}

	public List<Fol> getFoldersContaining(DocEntry de) {
		return getFoldersContaining(de.getId());
	}

	public Ro getObjectById(String id) {
		DocEntry de = docEntryCollection.getById(id);
		if (de != null) return de;
		SubSet ss = subSetCollection.getById(id);
		if (ss != null) return ss;
		Fol fol = folCollection.getById(id);
		if (fol != null) return fol;
		Assoc a = assocCollection.getById(id);
		if (a != null) return a;
		return null;
	}

	// this breaks delete
//	public Ro getObjectById2(String id) {
//		buildAllCollections();
//		for (RegObCollection c : allCollections) {
//			Ro ro = c.getRo(id);
//			if (ro != null)
//				return ro;
//		}
//		if (parent != null)
//			return parent.getObjectById(id);
//		return null;
//	}

	Map<String, Ro> objs = new HashMap<>();

	// includes objects being deleted
	public Map<String, Ro> buildTypeMap() {
		List<String> deletingSave = new ArrayList<>(docEntryCollection.idsBeingDeleted());
		setDeleting(new ArrayList<String>());

		objs.clear();
		for (DocEntry de : docEntryCollection.getAll())
			objs.put(de.id, de);
		for (SubSet ss : subSetCollection.getAll())
			objs.put(ss.id, ss);
		for (Assoc a : assocCollection.getAll())
			objs.put(a.id, a);
		for (Fol f : folCollection.getAll())
			objs.put(f.id, f);

		setDeleting(deletingSave);

		return objs;
	}

	// buildTypeMap must be called before using this
	public List<Ro> getObjectsOfType(List<String> ids, Class clas) {
		List<Ro> objects = new ArrayList<>();

		for (String id : ids) {
			Ro ro = objs.get(id);
			if (ro == null)
				continue;
			if (ro.getClass().equals(clas))
				objects.add(ro);
		}

		return objects;
	}

	public void addDocEntryToFolAssoc(DocEntry de, Fol f) throws MetadataException, XdsInternalException, IOException {
		addAssoc(f.getId(), de.getId(), AssocType.HasMember);
	}

	public void addDocEntryToFolAssoc(String deId, String fId) throws MetadataException, XdsInternalException, IOException {
		addAssoc(fId, deId, AssocType.HasMember);
	}


	public boolean hasObject(String id) {
		buildAllCollections();
		for (RegObCollection c : allCollections) {
			if (c.hasObject(id))
				return true;
		}
		return false;
	}

	Ro getObjectByUid(String uid) {
		buildAllCollections();
		for (RegObCollection c : allCollections) {
			Ro ro = c.getRoByUid(uid);
			if (ro != null)
				return ro;
		}
		return null;
	}

	public List<Ro> getObjectsByUid(String uid) {
		List<Ro> objs  = new ArrayList<>();
		buildAllCollections();
		for (RegObCollection c : allCollections) {
			Ro ro = c.getRoByUid(uid);
			if (ro != null)
				objs.add(ro);
		}
		return objs;
	}

	private Ro getRo(String id) {
		buildAllCollections();
		for (RegObCollection c : allCollections) {
			Ro ro = c.getRo(id);
			if (ro != null)
				return ro;
		}
		return null;
	}

	// compliment to storeMetadata(OMElement, boolean)
	public Metadata loadRo(String id) throws MetadataException, XdsInternalException {
		Ro ro = getRo(id);
		if (ro == null)
			return null;
		File f;
		if (ro.isPathIsRelative()) {
		 	f = regIndex.getAbsolutePathForObject(ro).toFile(); //ro.getFile();
		} else {
			String path = ro.getPathToMetadata();
			if (path == null)
				throw new ToolkitRuntimeException("Object " + id + " does not have a path to metadata stored in the index");
			f = new File(path);
		}
		Metadata m = MetadataParser.parseNonSubmission(f);
		attachAvailabilityStatus(m);
		m = attachFolderLastUpdateTime(m);
		return m;
	}

	public Metadata loadRo(Collection<String> ids) throws MetadataException, XdsInternalException {
		Metadata m = new Metadata();

		for (String id : ids) {
			Metadata m2 = loadRo(id);
			if (m2 == null)
				throw new XdsInternalException("Cannot load metadata for model " + id);
			m.addMetadata(m2);
		}


		return m;
	}

	private Metadata attachFolderLastUpdateTime(Metadata m) throws XdsInternalException, MetadataValidationException, MetadataException {

		boolean updateMade = false;
		for (OMElement ele : m.getFolders()) {
			String id = Metadata.getId(ele);
			try {
				Fol f = folCollection.getById(id);
				if (m.hasSlot(ele, "lastUpdateTime"))
					m.setSlotValue(ele, "lastUpdateTime", 0, f.lastUpdateTime);
				else
					m.addSlot(ele, "lastUpdateTime", f.lastUpdateTime);
				updateMade = true;
			}
			catch (Exception e) {
				throw new XdsInternalException("Internal Error: unable to attach lastUpdateTime to Folder " + id);
			}
		}
		if (updateMade)
			return m.reOrder();
		return m;
	}

	private void attachAvailabilityStatus(Metadata m) throws XdsInternalException {
		for (OMElement ele : m.getExtrinsicObjects()) {
			String id = Metadata.getId(ele);
			try {
				DocEntry de = docEntryCollection.getById(id);
				StatusValue sv = de.getAvailabilityStatus();
				String statusString = RegIndex.getStatusString(sv);
				ele.addAttribute("status", statusString, null);
			}
			catch (Exception e) {
				throw new XdsInternalException("Internal Error: unable to attach availabilityStatus to DocumentEntry " + id);
			}
		}

		for (OMElement ele : m.getFolders()) {
			String id = Metadata.getId(ele);
			try {
				Fol f = folCollection.getById(id);
				StatusValue sv = f.getAvailabilityStatus();
				String statusString = RegIndex.getStatusString(sv);
				ele.addAttribute("status", statusString, null);
			}
			catch (Exception e) {
				throw new XdsInternalException("Internal Error: unable to attach availabilityStatus to Folder " + id);
			}
		}

		for (OMElement ele : m.getSubmissionSets()) {
			String id = Metadata.getId(ele);
			try {
				SubSet s = subSetCollection.getById(id);
				StatusValue sv = s.getAvailabilityStatus();
				String statusString = RegIndex.getStatusString(sv);
				ele.addAttribute("status", statusString, null);
			}
			catch (Exception e) {
				throw new XdsInternalException("Internal Error: unable to attach availabilityStatus to SubmissionSet " + id);
			}
		}

		for (OMElement ele : m.getAssociations()) {
			String id = Metadata.getId(ele);
			try {
				Assoc a = assocCollection.getById(id);
				StatusValue sv = a.getAvailabilityStatus();
				String statusString = RegIndex.getStatusString(sv);
				ele.addAttribute("status", statusString, null);
			}
			catch (Exception e) {
				throw new XdsInternalException("Internal Error: unable to attach availabilityStatus to Association " + id);
			}
		}

	}

	private void idPresentCheck(Ro obj) throws MetadataException {
		if (parent != null && parent.hasObject(obj.id))
			throw new MetadataException("id " + obj.id + " (" + obj.getType() + ") already present in registry",null);
	}

	public List<String> getIdsForObjects(List objects) {

		List<Ro> objects2 = (List<Ro>) objects;

		List<String> ids = new ArrayList<String>();

		for (Ro object : objects2) {
			ids.add(object.id);
		}

		return ids;
	}

	public String statsToString() {
		StringBuffer buf = new StringBuffer();

		buf
				.append(docEntryCollection.statsToString()).append("\n")
				.append(folCollection.statsToString()).append("\n")
				.append(subSetCollection.statsToString()).append("\n")
				.append(assocCollection.statsToString()).append("\n");

		return buf.toString();
	}

	public void add(DocEntry de) throws MetadataException {
		idPresentCheck(de);
		docEntryCollection.getAllForUpdate().add(de);
		dirty = true;
	}

	public void add(Assoc a) throws MetadataException {
		idPresentCheck(a);
		assocCollection.getAllForUpdate().add(a);
		dirty = true;
	}

	public void add(SubSet ss) throws MetadataException {
		idPresentCheck(ss);
		subSetCollection.getAllForUpdate().add(ss);
		dirty = true;
	}

	public void add(Fol f) throws MetadataException {
		idPresentCheck(f);
		folCollection.getAllForUpdate().add(f);
		dirty = true;
	}

	// compliment to loadRo(String)
	private void storeMetadata(OMElement ele,  boolean overwriteOk) throws IOException, MetadataException, XdsInternalException {
		String id = Metadata.getId(ele);
		Ro ro = getRo(id);
		if (ro == null) {
			logger().debug("model " + id + " not found in metadata index");
			throw new XdsInternalException("MetadataCollection#storeMetadata: index corrupted");
		}

		File rof = regIndex.installInternalPath(ro);

		if (!overwriteOk && rof.exists())
			throw new MetadataException("Object with id " + id + " already exists in Registry and has type " + ro.getType(), null);

		OMElement wrapper = MetadataSupport.createElement("LeafRegistryObjectList", MetadataSupport.ebRIMns3);
		wrapper.addChild(ele);

		Io.stringToFile(rof, new OMFormatter(wrapper).toString());
	}

	public void storeMetadata(Metadata m, boolean overwriteOk) throws MetadataException, IOException, XdsInternalException {
//		logger().debug("storeMetadata:\n" + m.getSummary() + "\ngiven existing index:\n" + getStats("    "));
		for (OMElement ele : m.getExtrinsicObjects())
			storeMetadata(ele, overwriteOk);

		for (OMElement ele : m.getSubmissionSets())
			storeMetadata(ele, overwriteOk);

		for (OMElement ele : m.getFolders())
			storeMetadata(ele, overwriteOk);

		for (OMElement ele : m.getAssociations())
			storeMetadata(ele, overwriteOk);

	}

	public void storeMetadata(Metadata m) throws MetadataException, IOException, XdsInternalException {
		storeMetadata(m, false);
	}

	public boolean isDocumentEntry(String uuid) {
		return docEntryCollection.hasObject(uuid);
	}

	public boolean isFolder(String uuid) {
		return folCollection.hasObject(uuid);
	}

	public boolean isSubmissionSet(String uuid) {
		return subSetCollection.hasObject(uuid);
	}

	public void addAssoc(String source, String target, AssocType type) throws MetadataException, XdsInternalException, IOException {
		Assoc a = new Assoc();
		a.from = source;
		a.to = target;
		a.type = type;

		a.id = UuidAllocator.allocate();

		Metadata m = new Metadata();
		m.setVersion3();

		OMElement ele = m.mkAssociation(MetadataSupport.associationTypeWithNamespace(RegIndex.getAssocString(type)), source, target);
		ele.addAttribute("id", a.id, null);

		assocCollection.getAllForUpdate().add(a);

		storeMetadata(ele, false);
	}

	// for the registry objects that are associatons, validate out those who reference
	// model not in this set
	public List<Ro> filterAssocs(List<Ro> ros) {
		List<Ro> out = new ArrayList<Ro>();

		for (Ro ro : ros) {
			if (ro instanceof Assoc) {
				Assoc a = (Assoc) ro;
				if (!hasRo(ros, a.getFrom()))
					continue;
				if (!hasRo(ros, a.getTo()))
					continue;
				out.add(ro);
			} else {
				out.add(ro);
			}
		}

		return out;
	}

	public List<Assoc> filterAssocsByStatus(List<Assoc> assocs, SQStatusTerm status) {
	    List<Assoc> rets = new ArrayList<>();

	    if (status == null)
	        return rets;

	    for (Assoc a : assocs) {
	        if (a.isDeprecated() && status.isDeprecatedAceptable())
	            rets.add(a);
	        if (!a.isDeprecated() && status.isApprovedAceptable())
	            rets.add(a);
        }

	    return rets;
    }

	private boolean hasRo(List<Ro> ros, String id) {
		for (Ro ro : ros) {
			if (ro.getId().equals(id))
				return true;
		}
		return false;
	}

	public boolean isDirty() { return dirty; }
	void setDirty(boolean dirty) { this.dirty = dirty; }

	void clearAllCollections() { allCollections = null; }

	@Override
	public boolean hasRegistryIndex() {
		return regIndex.getSimDb().getRegistryIndexFile().exists();
	}

	public MetadataCollection getParent() {
		return parent;
	}
}
