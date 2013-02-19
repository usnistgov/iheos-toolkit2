package gov.nist.toolkit.results.client;

import gov.nist.toolkit.actortransaction.client.ATFactory.ActorType;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Lightweight recording of the selected Site parameters. Does
 * not interact with the Site class which has full
 * transaction details. This is used to aim the test engine
 * for firing at an actor implementation.
 * @author bill
 *
 */
public class SiteSpec implements IsSerializable {

	public String name = "";
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
			isTls = true;
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
		return actorType.isGW();
	}
	
	public boolean isIG() {
		return actorType.isIGActor();
	}
	
	public boolean isRG() {
		return actorType.isRGActor();
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
