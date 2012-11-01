package gov.nist.toolkit.xdstools2.client.inspector;

import gov.nist.toolkit.registrymetadata.client.MetadataDiff;
import gov.nist.toolkit.registrymetadata.client.MetadataObject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

class HistorySelector implements ClickHandler {
	MetadataObject mo;
	MetadataInspectorTab it;

	HistorySelector(MetadataInspectorTab it, MetadataObject o) {
		mo = o;
		this.it = it;
	}

	public void onClick(ClickEvent event) {
		it.detailPanel.clear();
//		it.displayDetail(mo);
		new DetailDisplay(it).displayDetail(mo, MetadataDiff.nullObject(mo));
		new StructureDisplay(it).displayStructure(mo);
	}

}

