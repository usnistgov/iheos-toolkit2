package gov.nist.toolkit.actorfactory.client;


import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.valsupport.client.ValidationContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

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
	String id;
	String type;
//	String[] values;   // these are possible values
	Date expires;
	boolean isExpired = false;
	List<SimulatorConfigElement> elements  = new ArrayList<SimulatorConfigElement>();
	
	// used to record RGs for use with an IG
	public List<String> remoteSiteNames = new ArrayList<String>();
	boolean remoteSitesNecessary = false;
	String remoteSitesLabel;
	// this is not a fixed attribute so it doesn't show in editor
	public List<Site> remoteSites = null;
	
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
//		for (ActorSimulatorConfigElement ele : asc.user) {
//			if (getUserByName(ele.name) == null)
//				user.add(ele);
//		}
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
		buf.append(" type=").append(type);
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
	
	public SimulatorConfig(String id, String type, Date expiration) {
		this.id = id;
		this.type = type;
		expires = expiration;
	}
	
	public List<SimulatorConfigElement> elements() {
		return elements;
	}
	
	public void add(List<SimulatorConfigElement> elementList) {
		elements.addAll(elementList);
	}
	
	public boolean areRemoteSitesNecessary() { return remoteSitesNecessary; }
	
	public void setRemoteSitesNecessary(boolean value, String displayLabel) {
		remoteSitesNecessary = value;
		remoteSitesLabel = displayLabel;
	}
	
	public String getRemoteSitesLabel() { return remoteSitesLabel; }
			
	public List<String> getRemoteSiteNames() { return remoteSiteNames; }
	public void setRemoteSiteNames(List<String> siteNames) { remoteSiteNames = siteNames;  }
	
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
	
	public SimulatorConfigElement	getUserByName(String name) {
		if (name == null)
			return null;
		
		for (SimulatorConfigElement ele : elements) {
			if (name.equals(ele.name))
				return ele;
		}
		return null;
	}
	
	public SimulatorConfigElement	getFixedByName(String name) {
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
	
	
	public String getId() {
		return id;
	}
	
	public String getType() {
		return type;
	}
	
	public SimulatorConfigElement get(String name) {
		for (SimulatorConfigElement ele : elements) {
			if (ele.name.equals(name))
				return ele;
		}
		return null;
	}
		
	public String getDefaultName() {
		return get("Name").asString() + "." + getType();
	}
	
	public ValidationContext getValidationContext() {
		return vc;
	}
	
	public void setValidationContext(ValidationContext vc) {
		this.vc = vc;
	}

//	public ActorFactory getActorFactory() throws Exception {
//		String simtype = getType();
//		ActorType at = ActorType.findActor(simtype);
//		ActorFactory af = ActorFactory.getActorFactory(at);
//		return af;
//	}

}