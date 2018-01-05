package gov.nist.toolkit.xdstools2.client.inspector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class XMLMainViewSelector implements ClickHandler {
	String xmlText;
	MetadataInspectorTab it;

	XMLMainViewSelector(MetadataInspectorTab it, String xmlText) {
		this.xmlText = xmlText.trim();
		this.it = it;
	}

	public void onClick(ClickEvent event) {
		it.detailPanel.clear();
		new DetailDisplay(it).displayText("Resource", xmlText);
	}


}
