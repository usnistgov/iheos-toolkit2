package gov.nist.toolkit.xdstools2.client.inspector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import gov.nist.toolkit.registrymetadata.client.MetadataObject;

/**
 * Metadata update selector
 */
class MuClickHandler implements ClickHandler {
	MetadataObject mo;
	MetadataInspectorTab it;

	MuClickHandler(MetadataInspectorTab it, MetadataObject o) {
		mo = o;
		this.it = it;
	}

	public void onClick(ClickEvent event) {
		it.detailPanel.clear();
		new EditDisplay(it).editDetail(mo);
	}

}

