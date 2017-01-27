package gov.nist.toolkit.actorfactory.client;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.actortransaction.client.ParamType;
import gov.nist.toolkit.simcommon.shared.config.SimulatorConfigElement;
import gov.nist.toolkit.valsupport.client.ValidationContext;

import java.io.Serializable;
import java.util.*;

/**
 * Definition for an actor simulator.
 * @author bill
 *
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class SimulatorConfig implements Serializable, IsSerializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Globally unique id for this simulator
	 */
	SimId id;
	private String actorType;
	private boolean expired = false;
	private List<SimulatorConfigElement> elements  = new ArrayList<SimulatorConfigElement>();

	// This is only used to record validation requirements for included document(s)
	// vc != null triggers UI to display selections from tk_props and accept
	// selection.
	private ValidationContext vc = null;
	private transient CcdaTypeSelection docTypeSelector;

    public boolean isExpired() { return expired; }
	public void isExpired(boolean is) { expired = is; }

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
	
	public SimulatorConfig(SimId id, String actorType) {
		this.id = id;
		this.actorType = actorType;
	}
	
	public List<SimulatorConfigElement> elements() {
		return elements;
	}
	
	public void add(List<SimulatorConfigElement> elementList) {
		elements.addAll(elementList);
	}
    public void add(SimulatorConfigElement ele) { elements.add(ele); }

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

	public List<SimulatorConfigElement> getEndpointConfigs() {
		List<SimulatorConfigElement> configs = new ArrayList<>();

		for (SimulatorConfigElement config : elements) {
			if (config.type == ParamType.ENDPOINT) {
				configs.add(config);
			}
		}

		return configs;
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
    
    /**
     * Removes configuration parameter with same name (if found) and adds
     * passed parameter.
    * @param replacement parameter to add/replace
    * @return true if existing parameter was replaced. false if no such 
    * parameter was found, and passed parameter was added.
    */
   public boolean replace(SimulatorConfigElement replacement) {
       boolean replaced = false;
       Iterator <SimulatorConfigElement> itr = elements.iterator();
       while(itr.hasNext()) {
          SimulatorConfigElement existing = itr.next();
          if (existing.name.equals(replacement.name)) {
             itr.remove();
             replaced = true;
             break;
          }
       }
       elements.add(replacement);
       return replaced;
    }
	
	
	public SimId getId() {
		return id;
	}
    public void setId(SimId simId) { id = simId; }
	
	public String getActorType() {
		return actorType;
	}
    public void setActorType(String type) { actorType = type; }

    public String actorTypeFullName() {
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