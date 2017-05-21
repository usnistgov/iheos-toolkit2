package gov.nist.toolkit.desktop.client.legacy.genericQueryTab;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;

class PidChangeHandler implements ChangeHandler {
	GenericQueryTab tab;
	
	PidChangeHandler(GenericQueryTab tab) {
		this.tab = tab;
	}

	@Override
	public void onChange(ChangeEvent event) {
		tab.setCommonPatientId(tab.pidTextBox.getText());
	}
	
}