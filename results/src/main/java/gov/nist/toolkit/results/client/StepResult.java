package gov.nist.toolkit.results.client;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.registrymetadata.client.Document;
import gov.nist.toolkit.registrymetadata.client.MetadataCollection;
import gov.nist.toolkit.registrymetadata.client.ObjectRef;
import gov.nist.toolkit.registrymetadata.client.ObjectRefs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * Result data for a test step
 */
public class StepResult implements IsSerializable, Serializable {
	private static final long serialVersionUID = 1L;
	public boolean status;
	String registryResponseStatus;
	public String section;
	public String stepName;
	MetadataCollection metadata;
	public List<Document> documents;
	public List<ObjectRef> toBeRetrieved;
	TestLog testLog = null;  // loaded separately by client
	boolean haveLogs = false;
	private List<String> soapFaults = new ArrayList<>();
	private List<String> errors = new ArrayList<>();

	public StepResult() {
		metadata = new MetadataCollection();
		toBeRetrieved = new ArrayList<ObjectRef>();
		status = true;
	}

	public StepResult clone() {
		StepResult r = new StepResult();
		r.status = status;
		r.section = section;
		r.stepName = stepName;
		r.metadata = metadata;
		r.documents = new ArrayList<Document>();
		for (Document d : documents) r.documents.add(d);
		r.toBeRetrieved = new ArrayList<ObjectRef>();
		for (ObjectRef o : toBeRetrieved) r.toBeRetrieved.add(o);
		r.testLog = testLog;
		r.haveLogs = haveLogs;
		return r;
	}

	public void addError(String err) {
		errors.add(err);
	}

	public List<String> getErrors() { return errors; }

    public String toString() {
        return
				section + "/" + stepName + ": " +
                metadata.docEntries.size() +
                " DocumentEntries  " +
                        metadata.submissionSets.size() +
                        " SubmissionSets  " +
                        metadata.assocs.size() +
                " Associations  " +
                        metadata.folders.size() +
                " Folders"
                ;
    }
	
	public boolean hasContent() {
		if (metadata == null) 
			return false;
		return metadata.hasContent();
	}
	
	public void setMetadata(MetadataCollection mc) {
		metadata = mc;
		resetToBeRetrieved();
	}

	public void resetToBeRetrieved() {
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

	public String getRegistryResponseStatus() {
		return registryResponseStatus;
	}

	public void setRegistryResponseStatus(String registryResponseStatus) {
		this.registryResponseStatus = registryResponseStatus;
	}

	public List<String> getSoapFaults() {
		return soapFaults;
	}

	public void setSoapFaults(List<String> soapFaults) {
		this.soapFaults = soapFaults;
	}
}
