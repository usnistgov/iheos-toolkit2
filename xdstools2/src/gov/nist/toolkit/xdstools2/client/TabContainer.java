package gov.nist.toolkit.xdstools2.client;

import gov.nist.toolkit.tk.client.TkProps;
import gov.nist.toolkit.xdstools2.client.tabs.EnvironmentState;
import gov.nist.toolkit.xdstools2.client.tabs.QueryState;
import gov.nist.toolkit.xdstools2.client.tabs.TestSessionState;

import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public interface TabContainer {
	public void addTab(VerticalPanel w, String title, boolean select);
	public TabPanel getTabPanel();
	public QueryState getQueryState();
	public EnvironmentState getEnvironmentState();
	public TestSessionState getTestSessionState();
}
