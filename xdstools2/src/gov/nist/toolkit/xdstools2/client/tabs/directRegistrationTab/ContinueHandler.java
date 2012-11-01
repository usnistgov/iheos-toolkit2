package gov.nist.toolkit.xdstools2.client.tabs.directRegistrationTab;

import gov.nist.toolkit.directsim.client.ContactRegistrationData;
import gov.nist.toolkit.xdstools2.client.PopupMessage;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ContinueHandler implements ClickHandler {
	DirectRegistrationTab tab;
	
	public ContinueHandler(DirectRegistrationTab tab) {
		this.tab = tab;
	}
	
	@Override
	public void onClick(ClickEvent arg0) {
		ContactRegistrationData data = null;
		try {
			data = tab.registrationData();
		} catch (Exception e) {
			new PopupMessage(e.getMessage());
			return;
		}
		tab.toolkitService.contactRegistration(data, registrationCallback);
	}
	
	AsyncCallback<ContactRegistrationData> registrationCallback = new AsyncCallback<ContactRegistrationData> () {

		@Override
		public void onFailure(Throwable arg0) {
			new PopupMessage("Error: " + arg0.getMessage());
		}

		@Override
		public void onSuccess(ContactRegistrationData arg0) {
			tab.contactMessage("Created");
		}
		
	};

}
