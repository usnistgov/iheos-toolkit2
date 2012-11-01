package gov.nist.toolkit.xdstools2.client.tabs.actorConfigTab;

import gov.nist.toolkit.xdstools2.client.LoadGazelleConfigs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

class ReloadSystemFromGazelleClickHandler implements ClickHandler {

	/**
	 * 
	 */
	private ActorConfigTab actorConfigTab;

	/**
	 * @param actorConfigTab
	 */
	ReloadSystemFromGazelleClickHandler(ActorConfigTab actorConfigTab) {
		this.actorConfigTab = actorConfigTab;
	}

	public void onClick(ClickEvent event) {
		String systemName = this.actorConfigTab.currentEditSite.getName();
		if (systemName == null || systemName.equals(""))
			return;
		new LoadGazelleConfigs(actorConfigTab.toolkitService, actorConfigTab.myContainer, systemName).load();
	}
	
}