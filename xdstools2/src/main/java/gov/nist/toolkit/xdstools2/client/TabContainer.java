package gov.nist.toolkit.xdstools2.client;

import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import gov.nist.toolkit.xdstools2.client.tabs.EnvironmentState;
import gov.nist.toolkit.xdstools2.client.tabs.QueryState;

public interface TabContainer {
	void addTab(VerticalPanel w, String title, boolean select);
	TabPanel getTabPanel();
	QueryState getQueryState();
	EnvironmentState getEnvironmentState();
}
