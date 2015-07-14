package gov.nist.toolkit.xdstools2.client.inspector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class XMLViewSelector implements ClickHandler {
	String xmlText;
	MetadataInspectorTab it;

	XMLViewSelector(MetadataInspectorTab it, String xmlText) {
		this.xmlText = xmlText.trim();
		this.it = it;
	}

	public void onClick(ClickEvent event) {
		it.structPanel.clear();
		new StructureDisplay(it).displayText(xmlText);
	}


}
