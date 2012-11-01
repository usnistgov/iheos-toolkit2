package gov.nist.toolkit.xdstools2.client.tabs.directRegistrationTab;

import gov.nist.toolkit.directsim.client.ContactRegistrationData;
import gov.nist.toolkit.directsim.client.DirectRegistrationData;
import gov.nist.toolkit.xdstools2.client.PopupMessage;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class DeleteDirectHandler implements ClickHandler {
	DirectRegistrationTab tab;

	public DeleteDirectHandler(DirectRegistrationTab tab) {
		this.tab = tab;
	}

	@Override
	public void onClick(ClickEvent arg0) {
		String direct = tab.currentDirectForDeletion();
		ContactRegistrationData contact;
		try {
			contact = tab.registrationData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return;
		}
		tab.toolkitService.deleteDirect(contact, new DirectRegistrationData(direct, contact.contactAddr), deleteCallback);
	}

	AsyncCallback<ContactRegistrationData> deleteCallback = new AsyncCallback<ContactRegistrationData> () {

		@Override
		public void onFailure(Throwable arg0) {
			new PopupMessage("Error: " + arg0.getMessage());
		}

		@Override
		public void onSuccess(ContactRegistrationData arg0) {
			tab.directDeleteExistingFromMessage("Deleted");
			tab.currentRegistration = arg0;
			tab.refreshContact();
		}

	};



}
