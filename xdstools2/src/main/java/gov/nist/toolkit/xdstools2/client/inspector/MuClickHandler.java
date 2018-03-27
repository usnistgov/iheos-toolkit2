package gov.nist.toolkit.xdstools2.client.inspector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import gov.nist.toolkit.registrymetadata.client.MetadataObject;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.TestInstance;

/**
 * Metadata update selector
 */
class MuClickHandler implements ClickHandler {
	MetadataObject mo;
	MetadataInspectorTab it;
	int idx;
	TestInstance logId;

	MuClickHandler(MetadataInspectorTab it, MetadataObject o, int idx, TestInstance logId) {
		mo = o;
		this.it = it;
		this.idx = idx;
		this.logId = logId;
	}

	public void onClick(ClickEvent event) {
		it.detailPanel.clear();
		new EditDisplay(it,mo,idx, logId);
	}

}

