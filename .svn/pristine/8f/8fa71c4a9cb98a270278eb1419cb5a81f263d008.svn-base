package gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;

class AsyncSelector implements ClickHandler {
	GenericQueryTab tab;

	AsyncSelector(GenericQueryTab tab) {
		this.tab = tab;
	}

	public void onClick(ClickEvent event) {
		tab.doASYNC = ((CheckBox) event.getSource()).getValue();		}

}