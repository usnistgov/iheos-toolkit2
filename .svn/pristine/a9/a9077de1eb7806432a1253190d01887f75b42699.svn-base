package gov.nist.toolkit.xdstools2.client.tabs.actorConfigTab;

import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.xdstools2.client.PopupMessage;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.TextBox;

class NameChangedHandler implements ChangeHandler {
	/**
	 * 
	 */
	private ActorConfigTab actorConfigTab;
	Site s;
	TextBox textbox;

	NameChangedHandler(ActorConfigTab actorConfigTab, Site s, TextBox textbox) {
		this.actorConfigTab = actorConfigTab;
		this.textbox = textbox;
		this.s = s;
	}


	public void onChange(ChangeEvent event) {
		String newName = textbox.getText().trim();
		if (this.actorConfigTab.currentSiteNames.contains(newName)) {
			new PopupMessage("Site " + newName + " already exists");
			return;
		}
		s.setName(newName);
		this.actorConfigTab.currentEditSite.changed = true;
	}

}