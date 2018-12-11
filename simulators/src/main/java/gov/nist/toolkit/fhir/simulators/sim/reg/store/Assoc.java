package gov.nist.toolkit.fhir.simulators.sim.reg.store;

import gov.nist.toolkit.fhir.simulators.sim.reg.store.RegIndex.AssocType;
import gov.nist.toolkit.utilities.id.UuidAllocator;
import gov.nist.toolkit.valregmsg.registry.SQStatusTerm;

import java.io.Serializable;

public class Assoc extends Ro implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public String from;
	public String to;
	public AssocType type;
	public boolean isOriginal;  // these two only apply to SubmissionSet to DocEntry HasMember
	public boolean isReference;

	public Assoc() {

	}

//	public Assoc(Ro from, Ro to, AssocType type) {
//		this.from = from.id;
//		this.to = to.id;
//		this.type = type;
//		this.id = UuidAllocator.allocate();
//		this.isDeprecated = false;
//	}

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

}
