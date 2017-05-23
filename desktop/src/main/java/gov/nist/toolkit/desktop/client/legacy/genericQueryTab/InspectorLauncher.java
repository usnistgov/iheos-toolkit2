package gov.nist.toolkit.desktop.client.legacy.genericQueryTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

class InspectorLauncher implements ClickHandler {
	private GenericQueryTab tab;
	
	InspectorLauncher(GenericQueryTab tab) {
		this.tab = tab;
	}
	
	public void onClick(ClickEvent event) {
//		MetadataInspectorTab itab = new MetadataInspectorTab();
//		itab.setResults(tab.results);
//		itab.setSiteSpec(tab.getCommonSiteSpec());
//		itab.onTabLoad(true, "Insp");
	}

}