package gov.nist.toolkit.xdstools2.client.tabs.actorConfigTab;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.TextBox;
import gov.nist.toolkit.sitemanagementui.client.Site;

class PifHostChangedHandler implements ValueChangeHandler {
	/**
	 *
	 */
	private ActorConfigTab actorConfigTab;
	Site site;
	TextBox textBox;

	PifHostChangedHandler(ActorConfigTab actorConfigTab, Site site, TextBox textBox) {
		this.actorConfigTab = actorConfigTab;
		this.site = site;
		this.textBox = textBox;
	}

	public void onValueChange(ValueChangeEvent event) {
		site.pifHost = textBox.getText().trim();
		actorConfigTab.currentEditSite.changed = true;
	}

}