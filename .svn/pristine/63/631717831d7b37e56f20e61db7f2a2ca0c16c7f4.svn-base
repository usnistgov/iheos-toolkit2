package gov.nist.toolkit.xdstools2.client.inspector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

class TextDisplay implements ClickHandler {
	/**
	 * 
	 */
	private final MetadataInspectorTab metadataInspectorTab;
	String text;

	TextDisplay(MetadataInspectorTab metadataInspectorTab, String text) {
		this.metadataInspectorTab = metadataInspectorTab;
		this.text = text;
	}

	public void onClick(ClickEvent event) {
		this.metadataInspectorTab.detailPanel.clear();
		this.metadataInspectorTab.detailPanel.add(HyperlinkFactory.addHTML(text));
	}

}