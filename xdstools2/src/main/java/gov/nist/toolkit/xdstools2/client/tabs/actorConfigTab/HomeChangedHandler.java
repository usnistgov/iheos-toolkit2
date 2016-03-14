package gov.nist.toolkit.xdstools2.client.tabs.actorConfigTab;

import gov.nist.toolkit.sitemanagement.client.Site;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.TextBox;

class HomeChangedHandler implements ChangeHandler {
	/**
	 * 
	 */
	private ActorConfigTab actorConfigTab;
	Site s;
	TextBox textbox;

	HomeChangedHandler(ActorConfigTab actorConfigTab, Site s, TextBox textbox) {
		this.actorConfigTab = actorConfigTab;
		this.textbox = textbox;
		this.s = s;
	}

	public void onChange(ChangeEvent event) {
		s.home = textbox.getText().trim();
		this.actorConfigTab.currentEditSite.changed = true;
	}

}