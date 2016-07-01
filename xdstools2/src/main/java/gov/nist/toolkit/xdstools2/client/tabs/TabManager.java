package gov.nist.toolkit.xdstools2.client.tabs;

import gov.nist.toolkit.xdstools2.client.ToolWindow;

import java.util.ArrayList;
import java.util.List;

public class TabManager {

	static List<ToolWindow> tabs = new ArrayList<ToolWindow>();

	public void addTab(int index, ToolWindow win) {
		while( index >= tabs.size())
			tabs.add(null);
		tabs.set(index, win);
	}

	public void notifyTabSelected(int index) {
		try {
			ToolWindow w = tabs.get(index);
			if (w == null) return;
			w.globalTabIsSelected();
		} catch (Exception e) {}
	}
	
	public void reset() {
		tabs.clear();
	}
	
}
