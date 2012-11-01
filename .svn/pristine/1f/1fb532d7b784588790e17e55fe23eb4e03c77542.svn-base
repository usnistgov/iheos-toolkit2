package gov.nist.toolkit.simulators.sim.reg.store;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymetadata.MetadataParser;
import gov.nist.toolkit.registrymetadata.UuidAllocator;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.simulators.sim.reg.store.RegIndex.AssocType;
import gov.nist.toolkit.simulators.sim.reg.store.RegIndex.OldValueNewValueStatus;
import gov.nist.toolkit.simulators.sim.reg.store.RegIndex.StatusValue;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.utilities.xml.OMFormatter;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.registry.RegistryValidationInterface;
import gov.nist.toolkit.xdsexception.MetadataException;
import gov.nist.toolkit.xdsexception.MetadataValidationException;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.axiom.om.OMElement;

public class MetadataCollection implements Serializable, RegistryValidationInterface {

	private static final long serialVersionUID = 1L;

	
	public DocEntryCollection docEntryCollection;
	public AssocCollection assocCollection;
	public SubSetCollection subSetCollection;
	public FolCollection folCollection;

	
	transient List<RegObCollection> allCollections = null;
	public transient FolCollection updatedFolCollection;
	transient boolean dirty;
	transient public RegIndex regIndex;
	transient public ValidationContext vc;

	// To maintain a delta ...
	transient MetadataCollection parent = null;
	transient List<OldValueNewValueStatus> statusChanges = null;
	
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
	
	public void changeAvailabilityStatus(String id, StatusValue oldValue, StatusValue newValue) {
		OldValueNewValueStatus ons = (new RegIndex()).new OldValueNewValueStatus(oldValue, newValue, id);
		statusChanges.add(ons);
	}
	
	// A delta has been created during the operation of a Register transaction
	// Merge the delta into the parent record
	// Caller takes responsibility for locking
	public boolean mergeDelta(ErrorRecorder er) {
		if (parent == null)
			return false;
		
		// do the parts that can fail first
		// first loop checks for problems, second makes changes
		
		for (OldValueNewValueStatus ons : statusChanges) {
			StatusValue oldVal = ons.o;
			String id = ons.id;
			
			Ro obj = getObjectById(id);
			if (obj == null) {
				er.err(Code.XDSRegistryError, "mergeDelta: cannot find object " + id, this, null);
				return false;
			}
			
			if (obj.getAvailabilityStatus() != oldVal) {
				er.err(Code.XDSRegistryError, "mergeDelta: old status has changed", this, null);
				return false;
			}
			
			ons.ro = obj;
			
		}

		for (OldValueNewValueStatus ons : statusChanges) {
			StatusValue newVal = ons.n;
			
			ons.ro.setAvailabilityStatus(newVal);
		}
		
		for (Fol f  : updatedFolCollection.fols) {
			String fid = f.getId();
			
			for (int i=0; i<parent.folCollection.fols.size(); i++) {
				Fol origFol = parent.folCollection.fols.get(i);
				
				if (fid.equals(origFol.getId())) {
					parent.folCollection.fols.set(i, f);
					break;
				}
			}
		}

		
		parent.docEntryCollection.entries.addAll(docEntryCollection.entries);

		parent.assocCollection.assocs.addAll(assocCollection.assocs);
		
		parent.folCollection.fols.addAll(folCollection.fols);
		
		parent.subSetCollection.subSets.addAll(subSetCollection.subSets);
		
		return true;
	}
	
	public void labelFolderUpdated(Fol f, String lastUpdateTime) {
		Fol nf = f.clone();
		nf.lastUpdateTime = lastUpdateTime;
		updatedFolCollection.fols.add(nf);
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

	void buildAllCollections() {
		if (allCollections == null) {
			allCollections = new ArrayList<RegObCollection>();
			allCollections.add(docEntryCollection);
			allCollections.add(assocCollection);
			allCollections.add(subSetCollection);
			allCollections.add(folCollection);
		}
	}

	public String deleteRo(String id) {

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
				roc.delete(id);
				
				// delete file containing xml
				File xml = ro.getFile();
				xml.delete();
				
				
			}
			return uid;
		}
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
	
	public List<Fol> getFoldersContaining(String id) {
		List<Fol> fols = new ArrayList<Fol>();
		
		List<Assoc> hasmembers = assocCollection.getBySourceDestAndType(null, id, AssocType.HASMEMBER);
		
		for (Assoc a : hasmembers) {
			if (isFolder(a.from))
				fols.add(folCollection.getById(a.from));
		}
		
		return fols;
	}
	
	public List<Fol> getFoldersContaining(DocEntry de) {
		return getFoldersContaining(de.getId());
	}

	public Ro getObjectById(String id) {
		buildAllCollections();
		for (RegObCollection c : allCollections) {
			Ro ro = c.getRo(id);
			if (ro != null)
				return ro;
		}
		return null;
	}
	
	public void addDocEntryToFolAssoc(DocEntry de, Fol f) throws MetadataException, XdsInternalException, IOException {
		addAssoc(f.getId(), de.getId(), AssocType.HASMEMBER); 
	}


	public boolean hasObject(String id) {
		buildAllCollections();
		for (RegObCollection c : allCollections) {
			if (c.hasObject(id))
				return true;
		}
		return false;
	}

	public Ro getObjectByUid(String uid) {
		buildAllCollections();
		for (RegObCollection c : allCollections) {
			Ro ro = c.getRoByUid(uid);
			if (ro != null)
				return ro;
		}
		return null;
	}

	public Ro getRo(String id) {
		buildAllCollections();
		for (RegObCollection c : allCollections) {
			Ro ro = c.getRo(id);
			if (ro != null)
				return ro;
		}
		return null;
	}

	public Metadata loadRo(String id) throws MetadataException, XdsInternalException {
		Ro ro = getRo(id);
		if (ro == null)
			return null;
		File f = ro.getFile();
		Metadata m = MetadataParser.parseNonSubmission(f);
		attachAvailabilityStatus(m);
		m = attachFolderLastUpdateTime(m);
		return m;
	}
	
	public Metadata loadRawRo(String id) throws MetadataValidationException, MetadataException, XdsInternalException {
		Ro ro = getRo(id);
		if (ro == null)
			return null;
		File f = ro.getFile();
		Metadata m = MetadataParser.parseNonSubmission(f);
		return m;
	}

	public Metadata loadRo(Collection<String> ids) throws MetadataException, XdsInternalException {
		Metadata m = new Metadata();

		for (String id : ids) {
			Metadata m2 = loadRo(id);
			if (m2 == null)
				throw new XdsInternalException("Cannot load metadata for object " + id);
			m.addMetadata(m2);
		}
		

		return m;
	}
	
	Metadata attachFolderLastUpdateTime(Metadata m) throws XdsInternalException, MetadataValidationException, MetadataException {

		boolean updateMade = false;
		for (OMElement ele : m.getFolders()) {
			String id = m.getId(ele);
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

	void attachAvailabilityStatus(Metadata m) throws XdsInternalException {
		for (OMElement ele : m.getExtrinsicObjects()) {
			String id = m.getId(ele);
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
			String id = m.getId(ele);
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
			String id = m.getId(ele);
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
			String id = m.getId(ele);
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

	public void idPresentCheck(Ro obj) throws MetadataException {
		if (hasObject(obj.id))
			throw new MetadataException("id " + obj.id + " already present in registry",null);
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
			docEntryCollection.entries.add(de);
			dirty = true;
	}

	public void add(Assoc a) throws MetadataException {
		idPresentCheck(a);
			assocCollection.assocs.add(a);
			dirty = true;
	}

	public void add(SubSet ss) throws MetadataException {
		idPresentCheck(ss);
			subSetCollection.subSets.add(ss);
			dirty = true;
	}

	public void add(Fol f) throws MetadataException {
		idPresentCheck(f);
			folCollection.fols.add(f);
			dirty = true;
	}

	public void storeMetadata(OMElement ele,  boolean overwriteOk) throws IOException, MetadataException, XdsInternalException {
		String id = new Metadata().getId(ele);
		Ro ro = getRo(id);
		if (ro == null)
			throw new XdsInternalException("MetadataCollection#storeMetadata: index corrupted");
		File rof = regIndex.getSimDb().getRegistryObjectFile(id);

		if (rof == null)
			throw new MetadataException("Object with id " + id + " cannot be persisted, the id must be a UUID", null);  

		if (!overwriteOk && rof.exists())
			throw new MetadataException("Object with id " + id + " already exists in Registry", null);

		ro.pathToMetadata = rof.toString();

		OMElement wrapper = MetadataSupport.createElement("LeafRegistryObjectList", MetadataSupport.ebRIMns3);
		wrapper.addChild(ele);


		Io.stringToFile(rof, new OMFormatter(wrapper).toString());
	}

	public void storeMetadata(Metadata m, boolean overwriteOk) throws MetadataException, IOException, XdsInternalException {
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
		
		assocCollection.assocs.add(a);
		
		storeMetadata(ele, false);
	}
	
	// for the registry objects that are associatons, filter out those who reference
	// object not in this set
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
	
	boolean hasRo(List<Ro> ros, String id) {
		for (Ro ro : ros) {
			if (ro.getId().equals(id))
				return true;
		}
		return false;
	}

}
