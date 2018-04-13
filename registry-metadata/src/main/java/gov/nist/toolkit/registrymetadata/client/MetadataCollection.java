package gov.nist.toolkit.registrymetadata.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MetadataCollection implements IsSerializable, Serializable {
	private static final long serialVersionUID = 1L;
	public String label;
	public List<DocumentEntry> docEntries = new ArrayList<>();
	public List<SubmissionSet> submissionSets = new ArrayList<>();
	public List<Folder> folders = new ArrayList<>();
	public List<Association> assocs = new ArrayList<>();
	public List<ObjectRef> objectRefs = new ArrayList<>();
	// if we don't understand the type then only the Stringified version
	// of it will be included
	public List<String> others = new ArrayList<>();
	// this is for resources other than those that map to XDS
	public List<ResourceItem> resources = new ArrayList<>();
	public boolean isFhir = false;

	public void setAllIsFhir(boolean fhir) {
		isFhir = fhir;
		for (DocumentEntry de : docEntries) de.isFhir = true;
		for (SubmissionSet ss : submissionSets) ss.isFhir = true;
		for (Folder f : folders) f.isFhir = true;
		for (Association as : assocs) as.isFhir = true;
		for (ObjectRef o : objectRefs) o.isFhir = true;
	}
	
	public MetadataObject findObject(String id) {
		for (MetadataObject ro : docEntries) {
			if (id.equals(ro.id))
				return ro;
		}
		for (MetadataObject ro : submissionSets) {
			if (id.equals(ro.id))
				return ro;
		}
		for (MetadataObject ro : folders) {
			if (id.equals(ro.id))
				return ro;
		}
		for (MetadataObject ro : assocs) {
			if (id.equals(ro.id))
				return ro;
		}
		for (MetadataObject ro : objectRefs) {
			if (id.equals(ro.id)) {
				return ro;
			}
		}
		return null;
	}
	
	public boolean hasContent() {
		return 
		docEntries.size() > 0 ||
		submissionSets.size() > 0 ||
		folders.size() > 0 ||
		assocs.size() > 0 ||
		objectRefs.size() > 0;
		
	}
	
	public MetadataCollection() {
		init();
	}
	
	public DocumentEntry getDocumentEntry(String idOrUid) {
		String id = "";
		String uid = "";
		if (idOrUid.indexOf("urn:uuid:") == -1) 
			uid = idOrUid;
		else
			id = idOrUid;
		
		for (DocumentEntry d : docEntries) {
			if (id.equals(d.id) || uid.equals(d.uniqueId))
				return  d;
		}
		return null;
	}
	
	public boolean hasDocumentEntry(String idOrUid) {
		return ! (getDocumentEntry(idOrUid) == null);
	}
	
	public void init() {
		docEntries = new ArrayList<DocumentEntry>();
		submissionSets = new ArrayList<SubmissionSet>();
		folders = new ArrayList<Folder>();
		assocs = new ArrayList<Association>();
		objectRefs = new ArrayList<ObjectRef>();
	}
	
	@SuppressWarnings("unchecked")
	void addNoDup(List to, List from) {
		for (Object f : from) {
			MetadataObject fmo = (MetadataObject) f;
			
			boolean exists = false;
			boolean sameIdButDifferentData = false;
			List<Object> oldO = new ArrayList<>();
			List<Object> newO = new ArrayList<>();
			for (Object t : to) {
				MetadataObject tmo = (MetadataObject) t;
				if (tmo.id != null && tmo.id.equals(fmo.id)) {
					if (tmo instanceof DocumentEntry) {
						if (! new DocumentEntryDiff().compare((DocumentEntry)t, (DocumentEntry)f).isEmpty()) {
								sameIdButDifferentData = true;
								oldO.add(t);
								newO.add(f);
						}
					}
					exists = true;
					break;
				}
			}

			if (sameIdButDifferentData && ! oldO.isEmpty()) {
			    for (int idx=0; idx < oldO.size(); idx++) {
			    	to.remove(oldO.get(idx));
			    	to.add(newO.get(idx));
				}
			}

			if (!exists)
				to.add(f);
		}
	}
	
	// does not allow duplicates
	public void add(MetadataCollection mc) {
		addNoDup(docEntries, mc.docEntries);
		addNoDup(submissionSets, mc.submissionSets);
		addNoDup(folders, mc.folders);
		addNoDup(assocs, mc.assocs);
		addNoDup(objectRefs, mc.objectRefs);
	}
}
