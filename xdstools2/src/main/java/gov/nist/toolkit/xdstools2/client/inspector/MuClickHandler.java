package gov.nist.toolkit.xdstools2.client.inspector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.results.client.TestInstance;

/**
 * Metadata update selector
 */
class MuClickHandler implements ClickHandler {
	DocumentEntry de;
	MetadataInspectorTab it;
	TestInstance logId;

	MuClickHandler(MetadataInspectorTab it, DocumentEntry de, TestInstance logId) {
		this.de = de;
		this.it = it;
		this.logId = logId;
	}

	public void onClick(ClickEvent event) {
		it.detailPanel.clear();
		new EditDisplay(it, de, logId);
	}

}

