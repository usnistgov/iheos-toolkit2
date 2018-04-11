package gov.nist.toolkit.xdstools2.client.inspector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;

/**
 * Metadata update selector
 */
class MuClickHandler implements ClickHandler {
	DocumentEntry de;
	MetadataInspectorTab it;
	TestInstance logId;
	QueryOrigin queryOrigin;

	MuClickHandler(MetadataInspectorTab it, DocumentEntry de, TestInstance logId, QueryOrigin queryOrigin) {
		this.de = de;
		this.it = it;
		this.logId = logId;
		this.queryOrigin = queryOrigin;
	}

	public void onClick(ClickEvent event) {
		it.detailPanel.clear();
//		it.showStructure(false);
		try {
			EditDisplay editDisplay = new EditDisplay(it, de, logId, queryOrigin);
			editDisplay.editDetail();
		} catch (Exception ex) {
			new PopupMessage(ex.toString());
		}
	}

}

