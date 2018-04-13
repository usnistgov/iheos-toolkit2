package gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.ListBox;
import gov.nist.toolkit.results.client.CodeConfiguration;
import gov.nist.toolkit.xdstools2.client.widgets.CodePicker;
import gov.nist.toolkit.xdstools2.client.widgets.queryFilter.StatusDisplay;

public class CodeEditButtonSelector implements ClickHandler {
	CodeConfiguration cc;
	ListBox toUpdate;
	StatusDisplay statusDisplay;

	public CodeEditButtonSelector(StatusDisplay statusDisplay, CodeConfiguration cc, ListBox listBoxToUpdate) {
		this.statusDisplay = statusDisplay;
		this.cc = cc;
		toUpdate = listBoxToUpdate;
	}
	public void onClick(ClickEvent event) {
		try {
			new CodePicker(cc, toUpdate).show();
		} catch (Exception e) {
			statusDisplay.setStatus(e.getMessage(), false);
		}
	}

}