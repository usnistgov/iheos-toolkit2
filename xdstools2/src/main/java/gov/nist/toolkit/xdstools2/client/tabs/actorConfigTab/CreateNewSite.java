package gov.nist.toolkit.xdstools2.client.tabs.actorConfigTab;

import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.xdstools2.client.PopupMessage;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

class CreateNewSite implements ClickHandler {

	/**
	 * 
	 */
	private ActorConfigTab actorConfigTab;

	/**
	 * @param actorConfigTab
	 */
	CreateNewSite(ActorConfigTab actorConfigTab) {
		this.actorConfigTab = actorConfigTab;
	}

	public void onClick(ClickEvent event) {
		if (this.actorConfigTab.currentEditSite != null && this.actorConfigTab.currentEditSite.changed == true) {
			new PopupMessage("Current Site has been edited. Save or Forget changes before creating new site");
			return;
		}
		Site site = new Site();
		site.setName(this.actorConfigTab.newSiteName);
		this.actorConfigTab.newActorEditGrid();
		this.actorConfigTab.displaySite(site);
	}

}