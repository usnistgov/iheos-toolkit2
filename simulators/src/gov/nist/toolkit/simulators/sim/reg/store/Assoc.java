package gov.nist.toolkit.simulators.sim.reg.store;

import gov.nist.toolkit.simulators.sim.reg.store.RegIndex.AssocType;

import java.io.Serializable;

public class Assoc extends Ro implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public String from;
	public String to;
	public AssocType type;
	
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
