package gov.nist.toolkit.metadataModel;

import java.io.Serializable;

public class SubSet extends PatientObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public String sourceId;
	public String submissionTime;
	public String[] authorNames;
	public String contentType;
	
	public String getType() {
		return "SubmissionSet";
	}
	

}
