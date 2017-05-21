package gov.nist.toolkit.desktop.client.legacy.genericQueryTab;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;

public class SamlSelector implements ChangeHandler {
	GenericQueryTab tab;

	public SamlSelector(GenericQueryTab tab) {
		this.tab = tab;
	}

	public void onChange(ChangeEvent event) {
		String selectedValue = ((ListBox)event.getSource()).getSelectedValue();

		if ("NoSaml".equals(selectedValue)) {
			tab.getSiteSelection().setSaml(false);
			tab.getCommonSiteSpec().setSaml(false);
			tab.getCommonSiteSpec().setGazelleXuaUsername(null);
		} else {
			tab.getSiteSelection().setSaml(true);
			tab.getCommonSiteSpec().setSaml(true);
			tab.getCommonSiteSpec().setGazelleXuaUsername(selectedValue);
		}
	}
}