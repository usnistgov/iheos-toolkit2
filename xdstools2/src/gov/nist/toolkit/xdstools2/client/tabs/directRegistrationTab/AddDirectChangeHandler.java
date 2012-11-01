package gov.nist.toolkit.xdstools2.client.tabs.directRegistrationTab;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;

public class AddDirectChangeHandler implements ChangeHandler {
	DirectRegistrationTab tab;
	
	public AddDirectChangeHandler(DirectRegistrationTab tab) {
		this.tab = tab;
	}

	@Override
	public void onChange(ChangeEvent arg0) {
		String direct = tab.addDirectFrom.getText();
		byte[] cert = tab.cert.getText().getBytes();
		
		if (direct == null) direct = "";
		if (cert == null) cert = new byte[0];;
		
		if (direct.equals(""))
			return;
		
		tab.currentRegistration.directToCertMap.put(direct, cert);
		
	}

}
