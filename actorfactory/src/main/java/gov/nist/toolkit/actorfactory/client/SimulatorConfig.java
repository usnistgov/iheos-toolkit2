package gov.nist.toolkit.actorfactory.client;


import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.valsupport.client.ValidationContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Definition for an actor simulator.
 * @author bill
 *
 */
public class SimulatorConfig implements Serializable, IsSerializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Globally unique id for this simulator
	 */
	SimId id;
	String actorType;
	Date expires;
	boolean isExpired = false;
	List<SimulatorConfigElement> elements  = new ArrayList<SimulatorConfigElement>();

	// This is only used to record validation requirements for included document(s)
	// vc != null triggers UI to display selections from tk_props and accept
	// selection.
	ValidationContext vc = null;
	transient CcdaTypeSelection docTypeSelector;

    public boolean isExpired() { return isExpired; }
	public void isExpired(boolean is) { isExpired = is; }

	public boolean checkExpiration() {
		Date now = new Date();
		if (now.after(expires))
			isExpired = true;
		else
			isExpired = false;
		return isExpired;
	}
	
	// not sure what to do with the other attributes, leave alone for now
	public void add(SimulatorConfig asc) {
		for (SimulatorConfigElement ele : asc.elements) {
			if (getFixedByName(ele.name) == null)
				elements.add(ele);
		}
	}
	
	/**
	 * Update ValidationContext from CCDA selection inside CcdaTypeSelection. Called
	 * just before saving SimulatorConfig back to server.
	 */
	public void updateDocTypeSelection() {
		if (docTypeSelector == null)
			return;
		vc.ccdaType = docTypeSelector.getCcdaContentType();
	}
	
	public void setDocTypeSelector(CcdaTypeSelection sel) {
		docTypeSelector = sel;
	}
	
	public CcdaTypeSelection getDocTypeSelector() {
		return docTypeSelector;
	}
		
	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("ActorSimulatorConfig:");
		buf.append(" id=").append(id);
		buf.append(" type=").append(actorType);
		buf.append("\n\telements=[");
		for (SimulatorConfigElement asce : elements) {
			buf.append("\n\t\t").append(asce);
		}
//		buf.append("\n\tuser=[");
//		for (ActorSimulatorConfigElement asce : user) {
//			buf.append("\n\t\t").append(asce);
//		}
		
		return buf.toString();
	}
	
	public SimulatorConfig() {
		
	}
	
	public SimulatorConfig(SimId id, String actorType, Date expiration) {
		this.id = id;
		this.actorType = actorType;
		expires = expiration;
	}
	
	public List<SimulatorConfigElement> elements() {
		return elements;
	}
	
	public void add(List<SimulatorConfigElement> elementList) {
		elements.addAll(elementList);
	}
    public void add(SimulatorConfigElement ele) { elements.add(ele); }

	public Date getExpiration() {
		return expires;
	}
	
	public List<SimulatorConfigElement> getFixed() {
		List<SimulatorConfigElement> fixed = new ArrayList<SimulatorConfigElement>();
		for (SimulatorConfigElement ele : elements) {
			if (!ele.isEditable())
				fixed.add(ele);
		}
		return fixed;
	}
	
	public List<SimulatorConfigElement> getElements() { return elements; }
	
	public List<SimulatorConfigElement> getUser() {
		List<SimulatorConfigElement> user = new ArrayList<SimulatorConfigElement>();
		for (SimulatorConfigElement ele : elements) {
			if (ele.isEditable())
				user.add(ele);
		}
		return user;
	}

    @Deprecated
	public SimulatorConfigElement	getUserByName(String name) {
		if (name == null)
			return null;
		
		for (SimulatorConfigElement ele : elements) {
			if (name.equals(ele.name))
				return ele;
		}
		return null;
	}

    @Deprecated
	public SimulatorConfigElement	getFixedByName(String name) {
		if (name == null)
			return null;
		
		for (SimulatorConfigElement ele : elements) {
			if (name.equals(ele.name))
				return ele;
		}
		return null;
	}

    public SimulatorConfigElement getConfigEle(String name) {
        if (name == null)
            return null;

        for (SimulatorConfigElement ele : elements) {
            if (name.equals(ele.name))
                return ele;
        }
        return null;
    }

    public void deleteFixedByName(String name) {
		SimulatorConfigElement ele = getFixedByName(name);
		if (ele != null)
			elements.remove(ele);
	}
	
	public void deleteUserByName(String name) {
		SimulatorConfigElement ele = getUserByName(name);
		if (ele != null)
			elements.remove(ele);
	}

    public boolean hasConfig(String name) {
        return getFixedByName(name) != null;
    }
	
	
	public SimId getId() {
		return id;
	}
    public void setId(SimId simId) { id = simId; }
	
	public String getActorType() {
		return actorType;
	}
    public void setActorType(String type) { actorType = type; }

    public String getActorTypeFullName() {
        String actorTypeName = getActorType();
        ActorType type = ActorType.findActor(actorTypeName);
        if (type == null) return actorTypeName;
        return type.getName();
    }
	
	public SimulatorConfigElement get(String name) {
		for (SimulatorConfigElement ele : elements) {
			if (ele.name.equals(name))
				return ele;
		}
		return null;
	}
		
	public String getDefaultName() {
		return get("Name").asString(); // + "." + getActorType();
	}
	
	public ValidationContext getValidationContext() {
		return vc;
	}
	
	public void setValidationContext(ValidationContext vc) {
		this.vc = vc;
	}

}