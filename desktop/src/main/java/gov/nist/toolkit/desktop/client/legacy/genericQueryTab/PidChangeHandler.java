package gov.nist.toolkit.desktop.client.legacy.genericQueryTab;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;

public class PidChangeHandler implements ChangeHandler {
	GenericQueryTab tab;
	
	public PidChangeHandler(GenericQueryTab tab) {
		this.tab = tab;
	}

	@Override
	public void onChange(ChangeEvent event) {
		tab.setCommonPatientId(tab.pidTextBox.getText());
	}
	
}