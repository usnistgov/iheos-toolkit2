package gov.nist.toolkit.xdstools2.client.tabs.actorConfigTab;

import gov.nist.toolkit.sitemanagement.client.TransactionBean;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.TextBox;

class EndpointChangedHandler implements ValueChangeHandler {
	/**
	 * 
	 */
	private ActorConfigTab actorConfigTab;
	TransactionBean transbean1 = null;
	TransactionBean transbean2 = null;
	TextBox textbox;

	EndpointChangedHandler(ActorConfigTab actorConfigTab, TransactionBean transbean1, TextBox textbox) {
		this.actorConfigTab = actorConfigTab;
		this.transbean1 = transbean1;
		this.textbox = textbox;
	}

	EndpointChangedHandler(ActorConfigTab actorConfigTab, TransactionBean transbean1, TransactionBean transbean2, TextBox textbox) {
		this.actorConfigTab = actorConfigTab;
		this.transbean1 = transbean1;
		this.transbean2 = transbean2;
		this.textbox = textbox;
	}

	public void onValueChange(ValueChangeEvent event) {
		transbean1.endpoint = textbox.getText().trim();
		if (transbean2 != null)
			transbean2.endpoint = transbean1.endpoint;
		actorConfigTab.currentEditSite.changed = true;
	}

}