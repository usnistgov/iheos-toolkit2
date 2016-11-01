package gov.nist.toolkit.xdstools2.client.tabs.actorConfigTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.command.command.GetSiteCommand;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.GetSiteRequest;

public class SiteChoose implements ClickHandler {
	/**
	 * 
	 */
	private ActorConfigTab actorConfigTab;

	/**
	 * @param actorConfigTab
	 */
	public SiteChoose(ActorConfigTab actorConfigTab) {
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
		String siteName=actorConfigTab.getSelectedValueFromListBox(actorConfigTab.siteSelector);
		getSite(siteName);
		currentSelection = actorConfigTab.siteSelector.getSelectedIndex();
	}

	public void editSite(SiteSpec siteSpec) {
		getSite(siteSpec.getName());
	}

	public void getSite(String siteName){
		new GetSiteCommand(){
			@Override
			public void onComplete(Site result) {
				actorConfigTab.displaySite(result);
			}
		}.run(new GetSiteRequest(ClientUtils.INSTANCE.getCommandContext(),siteName));
	}
}