package gov.nist.toolkit.desktop.client.legacy.genericQueryTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;

public class TlsSelector implements ClickHandler {
	GenericQueryTab tab;

	public TlsSelector(GenericQueryTab tab) {
		this.tab = tab;
	}

	public void onClick(ClickEvent event) {
        boolean value = ((CheckBox) event.getSource()).getValue();
		tab.getCommonSiteSpec().setTls(value);
		tab.redisplay(true);
	}

}