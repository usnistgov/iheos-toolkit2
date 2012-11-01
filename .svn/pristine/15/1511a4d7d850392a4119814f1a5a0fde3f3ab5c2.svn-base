package gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab;

import gov.nist.toolkit.results.client.CodeConfiguration;
import gov.nist.toolkit.xdstools2.client.CodePicker;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.ListBox;

public class CodeEditButtonSelector implements ClickHandler {
	CodeConfiguration cc;
	ListBox toUpdate;
	GenericQueryTab genericQueryTab;

	public CodeEditButtonSelector(GenericQueryTab genericQueryTab, CodeConfiguration cc, ListBox listBoxToUpdate) {
		this.genericQueryTab = genericQueryTab;
		this.cc = cc;
		toUpdate = listBoxToUpdate;
	}
	public void onClick(ClickEvent event) {
		try {
			new CodePicker(cc, toUpdate).show();
		} catch (Exception e) {
			genericQueryTab.setStatus(e.getMessage(), false);
		}
	}

}