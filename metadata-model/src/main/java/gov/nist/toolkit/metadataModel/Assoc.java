package gov.nist.toolkit.metadataModel;

import gov.nist.toolkit.metadataModel.RegIndex.AssocType;

import java.io.Serializable;

public class Assoc extends Ro implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public String from;
	public String to;
	public AssocType type;
	public boolean isOriginal;  // these two only apply to SubmissionSet to DocEntry HasMember
	public boolean isReference;
	
	public String getType() {
		return "Association(" + type + ")";
	}
	
	public AssocType getAssocType() {
		return type;
	}
	
	public String getFrom() {
		return from;
	}
	
	public String getTo() {
		return to;
	}
	
	public String toString() {
		return type.toString() + " source=" + from + " target=" + to;
	}

	public Assoc withFrom(String id) {
		from = id;
		return this;
	}

	public Assoc withFrom(Ro ro) {
		from = ro.id;
		return this;
	}

	public Assoc withTo(String id) {
		to = id;
		return this;
	}

	public Assoc withTo(Ro ro) {
		to = ro.id;
		return this;
	}

	public Assoc withType(AssocType theType) {
		type = theType;
		return this;
	}

}
