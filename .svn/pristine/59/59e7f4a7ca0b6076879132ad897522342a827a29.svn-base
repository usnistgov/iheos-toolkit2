package gov.nist.toolkit.registrymetadata.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class MetadataCollection implements IsSerializable, Serializable {
	private static final long serialVersionUID = 1L;
	public String label;
	public List<DocumentEntry> docEntries;
	public List<SubmissionSet> submissionSets;
	public List<Folder> folders;
	public List<Association> assocs;
	public List<ObjectRef> objectRefs;
	
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
			for (Object t : to) {
				MetadataObject tmo = (MetadataObject) t;
				if (tmo.id != null && tmo.id.equals(fmo.id)) { 
					exists = true;
					break;
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
