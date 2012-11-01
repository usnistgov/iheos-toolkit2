package gov.nist.toolkit.xdstools2.client.tabs.actorConfigTab;

import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.xdstools2.client.PopupMessage;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

class SiteChoose implements ClickHandler {
	/**
	 * 
	 */
	private ActorConfigTab actorConfigTab;

	/**
	 * @param actorConfigTab
	 */
	SiteChoose(ActorConfigTab actorConfigTab) {
		this.actorConfigTab = actorConfigTab;
	}

	int currentSelection = -1;  

	public void onClick(ClickEvent event) {
		if (actorConfigTab.currentEditSite != null && actorConfigTab.currentEditSite.changed == true && currentSelection != -1) {
			new PopupMessage("Current Site has been edited. Save or Forget changes before selecting a different site");
			actorConfigTab.siteSelector.setSelectedIndex(currentSelection);
			return;
		}
		actorConfigTab.newActorEditGrid();
		actorConfigTab.toolkitService.getSite(
				actorConfigTab.getSelectedValueFromListBox(actorConfigTab.siteSelector), 
				loadSiteCallback);
		currentSelection = actorConfigTab.siteSelector.getSelectedIndex();
	}
	
	protected AsyncCallback<Site> loadSiteCallback = new AsyncCallback<Site>() {

		public void onFailure(Throwable caught) {
			new PopupMessage(caught.getMessage());
		}

		public void onSuccess(Site result) {
			actorConfigTab.displaySite(result);
		}

	};


}