package gov.nist.toolkit.xdstools2.client.tabs.directRegistrationTab;

import gov.nist.toolkit.directsim.client.ContactRegistrationData;
import gov.nist.toolkit.xdstools2.client.PopupMessage;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class LoadContactHandler implements ClickHandler, ChangeHandler {
	DirectRegistrationTab tab;

	public LoadContactHandler(DirectRegistrationTab tab) {
		this.tab = tab;
	}

	@Override
	public void onClick(ClickEvent arg0) {
		String contact = tab.contactEmail.getText().trim();
		if (contact != null && !contact.equals("")) {
			tab.toolkitService.loadDirectRegistration(contact, initialLookupCallback);
		}
	}

	// starting with no idea if this already exists
	AsyncCallback<ContactRegistrationData> initialLookupCallback = new AsyncCallback<ContactRegistrationData> () {

		@Override
		public void onFailure(Throwable arg0) {
			// not found - create
			tab.currentRegistration = new ContactRegistrationData();
			tab.currentRegistration.contactAddr = tab.contactEmail.getText();
			tab.toolkitService.contactRegistration(tab.currentRegistration, createCallback);
			tab.contactMessage("Created");
			tab.refreshContact();
		}

		@Override
		public void onSuccess(ContactRegistrationData arg0) {
			if (arg0 == null) {
				tab.currentRegistration = new ContactRegistrationData();
				tab.currentRegistration.contactAddr = tab.contactEmail.getText();
				tab.toolkitService.contactRegistration(tab.currentRegistration, createCallback);
				tab.contactMessage("Created");
				tab.refreshContact();
			} else {
				tab.contactMessage("Already exists - loaded");
				tab.currentRegistration = arg0;
				tab.refreshContact();
			}
		}

	};

	AsyncCallback<ContactRegistrationData> createCallback = new AsyncCallback<ContactRegistrationData> () {

		@Override
		public void onFailure(Throwable arg0) {
			new PopupMessage("Error: " + arg0.getMessage());
		}

		@Override
		public void onSuccess(ContactRegistrationData arg0) {
			tab.currentRegistration = arg0;
			tab.refreshContact();
		}

	};

	@Override
	public void onChange(ChangeEvent arg0) {
		String contact = tab.contactEmail.getText();
		if (contact != null && !contact.equals("")) {
			tab.toolkitService.loadDirectRegistration(contact, initialLookupCallback);
		}
	}

}
