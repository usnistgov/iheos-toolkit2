package gov.nist.toolkit.xdstools2.client.tabs.actorConfigTab;

import gov.nist.toolkit.sitemanagement.client.TransactionBean;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.TextBox;

class RepuidChangedHandler implements ChangeHandler {
	/**
	 * 
	 */
	private ActorConfigTab actorConfigTab;
	TransactionBean transbean;
	TextBox textbox;

	RepuidChangedHandler(ActorConfigTab actorConfigTab, TransactionBean transbean, TextBox textbox) {
		this.actorConfigTab = actorConfigTab;
		this.transbean = transbean;
		this.textbox = textbox;
	}

	public void onChange(ChangeEvent event) {
		transbean.setName(actorConfigTab.trim(textbox.getText()));
		if (transbean.getName() == null || transbean.getName().equals(""))
			transbean.setName("No Name");
		actorConfigTab.currentEditSite.changed = true;
	}

}