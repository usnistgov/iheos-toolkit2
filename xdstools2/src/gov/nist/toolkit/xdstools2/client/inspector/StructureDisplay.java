package gov.nist.toolkit.xdstools2.client.inspector;

import gov.nist.toolkit.registrymetadata.client.Association;
import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.registrymetadata.client.Folder;
import gov.nist.toolkit.registrymetadata.client.MetadataCollection;
import gov.nist.toolkit.registrymetadata.client.MetadataObject;
import gov.nist.toolkit.registrymetadata.client.ObjectRef;
import gov.nist.toolkit.registrymetadata.client.SubmissionSet;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class StructureDisplay {
	VerticalPanel structPanel;
	MetadataCollection metadataCollection;
	MetadataInspectorTab it;
	
	public StructureDisplay(MetadataInspectorTab it) {
		this.structPanel = it.structPanel;
		this.metadataCollection = it.data.combinedMetadata;
		this.it = it;
	}
	
	void displayText(String text) {
		structPanel.clear();
		HTML textW = new HTML();
		textW.setHTML(text);
		structPanel.add(textW);
	}

	void displayStructure(MetadataObject mo) {
		if (it.freezeStructDisplay)
			return;
		
		structPanel.clear();
		
		structPanel.add(HyperlinkFactory.addHTML("<h4>Structure</h4>"));
		
		CheckBox freezeBox = new CheckBox("freeze");
		freezeBox.setValue(it.freezeStructDisplay);
		freezeBox.addClickHandler(new FreezeBoxClickHandler(this));
		structPanel.add(freezeBox);
		
		FlexTable ft = new FlexTable();
		if (mo instanceof SubmissionSet) 
			displayStructure((SubmissionSet) mo, ft);
		if (mo instanceof DocumentEntry) 
			displayStructure((DocumentEntry) mo, ft);
		if (mo instanceof Folder) 
			displayStructure((Folder) mo, ft);
		if (mo instanceof Association) 
			displayStructure((Association) mo, ft);
		if (mo instanceof ObjectRef) 
			displayStructure((ObjectRef) mo, ft);

		structPanel.add(ft);
	}
	
	class FreezeBoxClickHandler implements ClickHandler {
		StructureDisplay sd;
		
		FreezeBoxClickHandler(StructureDisplay sd) {
			this.sd = sd;
		}
		public void onClick(ClickEvent event) {
			sd.it.freezeStructDisplay = ((CheckBox) event.getSource()).getValue();
		}
		
	}


	void displayStructure(Association assoc, FlexTable ft) {
		int row=0;

		ft.setWidget(row, 0, HyperlinkFactory.link(it, metadataCollection, assoc.id, assoc.type));
		row++;

		ft.setText(row, 1, "source");
		ft.setWidget(row, 2, HyperlinkFactory.link(it, metadataCollection, new ObjectRef(assoc.source, assoc.home)));
		row++;

		ft.setText(row, 1, "target");
		ft.setWidget(row, 2, HyperlinkFactory.link(it, metadataCollection, new ObjectRef(assoc.target, assoc.home)));
		row++;

	}

	void displayStructure(DocumentEntry de, FlexTable ft) {
		int row=0;

		ft.setWidget(row, 0, HyperlinkFactory.link(it, metadataCollection, de.id, de.displayName()));
		row++;

		for (Association a : metadataCollection.assocs) {
			if (de.id.equals(a.source)) {
				ft.setWidget(row, 1, HyperlinkFactory.link(it, metadataCollection, a.id, a.type));
				ft.setWidget(row, 2, HyperlinkFactory.link(it, metadataCollection, new ObjectRef(a.target, a.home)));
				row++;
			}
			if (de.id.equals(a.target)) {
				ft.setWidget(row, 1, HyperlinkFactory.link(it, metadataCollection, a.id, reverseAssociationType(a.type)));
				ft.setWidget(row, 2, HyperlinkFactory.link(it, metadataCollection, new ObjectRef(a.source, a.home)));
				row++;
			}
		}
	}

	void displayStructure(Folder fol, FlexTable ft) {
		int row=0;

		ft.setWidget(row, 0, HyperlinkFactory.link(it, metadataCollection, fol.id, fol.displayName()));
		row++;

		for (Association a : metadataCollection.assocs) {
			if (fol.id.equals(a.source)) {
				ft.setWidget(row, 1, HyperlinkFactory.link(it, metadataCollection, a.id, a.type));
				ft.setWidget(row, 2, HyperlinkFactory.link(it, metadataCollection, new ObjectRef(a.target, a.home)));
				row++;
			}
			if (fol.id.equals(a.target)) {
				ft.setWidget(row, 1, HyperlinkFactory.link(it, metadataCollection, a.id, reverseAssociationType(a.type)));
				ft.setWidget(row, 2, HyperlinkFactory.link(it, metadataCollection, new ObjectRef(a.source, a.home)));
				row++;
			}
		}

	}

	void displayStructure(SubmissionSet ss, FlexTable ft) {
		int row=0;

		ft.setWidget(row, 0, HyperlinkFactory.link(it, metadataCollection, ss.id, ss.displayName()));
		row++;

		for (Association a : metadataCollection.assocs) {
			if (ss.id.equals(a.source)) {
				ft.setWidget(row, 1, HyperlinkFactory.link(it, metadataCollection, a.id, a.type));
				ft.setWidget(row, 2, HyperlinkFactory.link(it, metadataCollection, new ObjectRef(a.target, a.home)));
				row++;
			}
			if (ss.id.equals(a.target)) {
				ft.setWidget(row, 1, HyperlinkFactory.link(it, metadataCollection, a.id, reverseAssociationType(a.type)));
				ft.setWidget(row, 2, HyperlinkFactory.link(it, metadataCollection, new ObjectRef(a.source, a.home)));
				row++;
			}
		}

	}

	void displayStructure(ObjectRef o, FlexTable ft) {
		int row=0;

		ft.setText(row, 0, o.id);
	}

	String reverseAssociationType(String type) {
		if ("HasMember".equals(type)) return "MemberOf";
		if ("RPLC".equals(type)) return "ReplacedBy";
		return "Reverse " + type;
	}

}
