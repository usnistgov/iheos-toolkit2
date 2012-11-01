package gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;

class SamlSelector implements ChangeHandler {
	GenericQueryTab tab;

	SamlSelector(GenericQueryTab tab) {
		this.tab = tab;
	}
	public void onChange(ChangeEvent event) {
		int selectedIndex= ((ListBox)event.getSource()).getSelectedIndex();
		if( selectedIndex == 0) {
			tab.getCommonSiteSpec().setSaml(false);
		}
		else if(selectedIndex == 1) {
			tab.getCommonSiteSpec().setSaml(true);
		}

	}
}