package gov.nist.toolkit.xdstools2.client.tabs.actorConfigTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

class ReloadClickHandler implements ClickHandler {

	/**
	 * 
	 */
	private ActorConfigTab actorConfigTab;

	/**
	 * @param actorConfigTab
	 */
	ReloadClickHandler(ActorConfigTab actorConfigTab) {
		this.actorConfigTab = actorConfigTab;
	}

	public void onClick(ClickEvent event) {
		actorConfigTab.loadExternalSites();
	}

}