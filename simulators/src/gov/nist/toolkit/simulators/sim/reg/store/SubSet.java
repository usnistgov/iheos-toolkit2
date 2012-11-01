package gov.nist.toolkit.simulators.sim.reg.store;

import java.io.Serializable;

public class SubSet extends Ro implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public String pid;
	public String sourceId;
	public String submissionTime;
	public String[] authorNames;
	public String contentType;
	
	public String getType() {
		return "SubmissionSet";
	}
	

}
