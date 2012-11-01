package gov.nist.toolkit.results.client;

import gov.nist.toolkit.registrymetadata.client.Document;
import gov.nist.toolkit.registrymetadata.client.MetadataCollection;
import gov.nist.toolkit.registrymetadata.client.ObjectRef;
import gov.nist.toolkit.registrymetadata.client.ObjectRefs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class StepResult implements IsSerializable, Serializable {
	private static final long serialVersionUID = 1L;
	public boolean status;
	public String section;
	public String stepName;
	MetadataCollection metadata;
	public List<Document> documents;
	public List<ObjectRef> toBeRetrieved;
	TestLog testLog = null;  // loaded separately by client
	boolean haveLogs = false;

	public StepResult() {
		metadata = new MetadataCollection();
		toBeRetrieved = new ArrayList<ObjectRef>();
		status = true;
	}
	
	public boolean hasContent() {
		if (metadata == null) 
			return false;
		return metadata.hasContent();
	}
	
	public void setMetadata(MetadataCollection mc) {
		metadata = mc;
		toBeRetrieved = new ArrayList<ObjectRef>();
		toBeRetrieved.addAll(metadata.objectRefs);
	}
	
	public MetadataCollection getMetadata() {
		return metadata;
	}

	public ObjectRefs nextNObjectRefs(int n) {
		ObjectRefs ors = new ObjectRefs();
		
		for (int i=0; i<n && i < toBeRetrieved.size(); i++) {
			ors.objectRefs.add(toBeRetrieved.get(i));
		}
		return ors;
	}
	
	public void rmFromToBeRetrieved(ObjectRefs ors) {
		toBeRetrieved.removeAll(ors.objectRefs);
	}
	
	public List<ObjectRef> getObjectRefs() {
		return toBeRetrieved;
	}
	
	public int getObjectRefCount() {
		return toBeRetrieved.size();
	}

	public boolean haveLogs() { return haveLogs; }
	
	public TestLog getTestLog() { return testLog; }
	
	public void setTestLog(TestLog tl) { testLog = tl; haveLogs = true;}
	
}
