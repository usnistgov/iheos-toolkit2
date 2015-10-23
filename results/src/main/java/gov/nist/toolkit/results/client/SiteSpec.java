package gov.nist.toolkit.results.client;

import gov.nist.toolkit.actortransaction.client.ActorType;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A Site is the collection of endpoints and parameters for a single site or as Gazelle calls it a system.
 * A Site references multiple actor types but it can hold only one copy of an actor type:
 * one Registry, one Repository etc.
 * A SiteSpec is a reference to a Site and a selection of one actor type. Having a SiteSpec you know
 * exactly which transactions are possible.
 *
 * SiteSpec reference the Site through the name attribute.
 * @author bill
 *
 */
public class SiteSpec implements IsSerializable {

	public String name = "";   // site name
	public ActorType actorType = null;
	public String homeId = "";
	public String homeName = "";
	public boolean isTls = false;
	public boolean isSaml = false;
	public boolean isAsync = false;

	public SiteSpec(String name, ActorType actorType, SiteSpec toClone) {
		this.name = name;
		this.actorType = actorType;
		
		if (toClone == null) {
			isTls = false;
			isSaml = false;
			isAsync = false;
		} else {
			isTls = toClone.isTls;
			isSaml = toClone.isSaml;
			isAsync = toClone.isAsync;
		}
	}
	
	public SiteSpec() {
		this("", null, null);
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		buf.append("SiteSpec: ").append(name).append(" (").append(actorType).append(") ");
		buf.append((isTls) ? " isTLS" : " notTls");
		buf.append((isSaml) ? " isSaml" : " notSaml");
				
		return buf.toString();
	}
	
	public boolean isGW() {
		return (actorType != null) && actorType.isGW();
	}
	
	public boolean isIG() {
		return (actorType != null) && actorType.isIGActor();
	}
	
	public boolean isRG() {
		return (actorType != null) && actorType.isRGActor();
	}
	
	public String getTypeName() {
		return actorType.getShortName();
	}

	public String getName() {
		return name;
	}

	public ActorType getActorType() {
		return actorType;
	}

	public boolean isTls() {
		return isTls;
	}

	public boolean isSaml() {
		return isSaml;
	}

	public void setTls(boolean isTls) {
		this.isTls = isTls;
	}

	public void setSaml(boolean isSaml) {
		this.isSaml = isSaml;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setActorType(ActorType actorType) {
		this.actorType = actorType;
	}
}
