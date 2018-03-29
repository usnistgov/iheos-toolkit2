package gov.nist.toolkit.xdstools2.client.tabs.actorConfigTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.xdstools2.client.Xdstools2;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;

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
		if (!Xdstools2.getInstance().isSystemSaveEnabled()) {
			new PopupMessage("You don't have permission to create a new System in this Test Session");
			return;
		}
		Site site = new Site(ClientUtils.INSTANCE.getCurrentTestSession());
		site.setOwner(site.getTestSession().getValue());
		site.setName(this.actorConfigTab.newSiteName);
		this.actorConfigTab.newActorEditGrid();
		this.actorConfigTab.displaySite(site);
	}

}