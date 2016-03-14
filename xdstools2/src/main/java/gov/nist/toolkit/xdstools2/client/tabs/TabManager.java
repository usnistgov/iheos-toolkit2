package gov.nist.toolkit.xdstools2.client.tabs;

import gov.nist.toolkit.xdstools2.client.TabbedWindow;

import java.util.ArrayList;
import java.util.List;

public class TabManager {

	static List<TabbedWindow> tabs = new ArrayList<TabbedWindow>();

	public void addTab(int index, TabbedWindow win) {
		while( index >= tabs.size())
			tabs.add(null);
		tabs.set(index, win);
	}

	public void notifyTabSelected(int index) {
		try {
			TabbedWindow w = tabs.get(index);
			if (w == null) return;
			w.globalTabIsSelected();
		} catch (Exception e) {}
	}
	
	public void reset() {
		tabs.clear();
	}
	
}
