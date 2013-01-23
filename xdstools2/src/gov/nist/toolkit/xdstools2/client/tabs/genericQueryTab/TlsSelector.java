package gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;

class TlsSelector implements ClickHandler {
	GenericQueryTab tab;

	TlsSelector(GenericQueryTab tab) {
		this.tab = tab;
	}

	public void onClick(ClickEvent event) {
		tab.getCommonSiteSpec().setTls(((CheckBox) event.getSource()).getValue());
		tab.redisplay();
	}

}