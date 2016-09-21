package gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import gov.nist.toolkit.xdstools2.client.inspector.MetadataInspectorTab;

class InspectorLauncher implements ClickHandler {
	GenericQueryTab tab;
	
	InspectorLauncher(GenericQueryTab tab) {
		this.tab = tab;
	}
	
	public void onClick(ClickEvent event) {
		MetadataInspectorTab itab = new MetadataInspectorTab();
		itab.setResults(tab.results);
		itab.setSiteSpec(tab.getCommonSiteSpec());
//		itab.setToolkitService(tab.toolkitService);
		itab.onTabLoad(true, "Insp");
	}

}