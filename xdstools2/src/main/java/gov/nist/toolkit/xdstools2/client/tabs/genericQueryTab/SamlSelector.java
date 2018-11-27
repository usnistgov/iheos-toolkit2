package gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;

public class SamlSelector implements ChangeHandler {
	GenericQueryTab tab;

	public SamlSelector(GenericQueryTab tab) {
		this.tab = tab;
	}

	public void onChange(ChangeEvent event) {

		if (tab!= null) {
			String selectedValue = ((ListBox)event.getSource()).getSelectedValue();

			boolean noSaml = "NoSaml".equals(selectedValue);
			String xuaUserName = (noSaml) ? null : selectedValue;
			if (tab.getSiteSelection()!=null)
				tab.getSiteSelection().setSaml(noSaml);
			if (tab.getCommonSiteSpec()!=null) {
				tab.getCommonSiteSpec().setSaml(noSaml);
				tab.getCommonSiteSpec().setGazelleXuaUsername(xuaUserName);
			}
		}

	}
}