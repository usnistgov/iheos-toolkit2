package gov.nist.toolkit.xdstools2.client.inspector;

import gov.nist.toolkit.registrymetadata.client.Association;
import gov.nist.toolkit.registrymetadata.client.Document;
import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.registrymetadata.client.Folder;
import gov.nist.toolkit.registrymetadata.client.ObjectRef;
import gov.nist.toolkit.registrymetadata.client.ObjectRefs;
import gov.nist.toolkit.registrymetadata.client.SubmissionSet;
import gov.nist.toolkit.xdstools2.client.TabContainer;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TreeItem;

public class ListingDisplay {
	MetadataInspectorTab tab;
	DataModel data;
	TreeThing root;
	
	public ListingDisplay(MetadataInspectorTab tab, DataModel data, TreeThing root) {
		this.tab = tab;
		this.data = data;
		this.root = root;
	}
	
	void listing(TabContainer container) {
		for (SubmissionSet ss : data.combinedMetadata.submissionSets) {
			TreeItem item = new TreeItem(HyperlinkFactory.link(tab, ss));
			root.addItem(item);
		}

		for (Folder fol : data.combinedMetadata.folders) {
			TreeItem item = new TreeItem(HyperlinkFactory.link(tab, fol));

			if (data.enableActions) {
				TreeItem getContentsItem = new TreeItem(HyperlinkFactory.getFolderAndContents(tab, new ObjectRef(fol.id, fol.home), "Action: Get Contents"));
				item.addItem(getContentsItem);
				
				TreeItem getSubmissionSetItem = new TreeItem(HyperlinkFactory.getSubmissionSets(tab, new ObjectRef(fol.id, fol.home), "Action: Get Submission Set"));
				item.addItem(getSubmissionSetItem);

				TreeItem getAssociationItem = new TreeItem(HyperlinkFactory.getAssociations(tab, new ObjectRef(fol.id, fol.home), "Action: Get Associations"));
				item.addItem(getAssociationItem);
			}

			root.addItem(item);
		}

		for (DocumentEntry de : data.combinedMetadata.docEntries) {
			TreeItem item = new TreeItem(HyperlinkFactory.link(tab, de));

			if (data.enableActions) {
				TreeItem getRelatedItem = new TreeItem(HyperlinkFactory.getRelated(tab, new ObjectRef(de.id, de.home), "Action: Get Related Documents"));
				item.addItem(getRelatedItem);

				TreeItem getSubmissionSetItem = new TreeItem(HyperlinkFactory.getSubmissionSets(tab, new ObjectRef(de.id, de.home), "Action: Get Submission Set"));
				item.addItem(getSubmissionSetItem);

				TreeItem getAssociationItem = new TreeItem(HyperlinkFactory.getAssociations(tab, new ObjectRef(de.id, de.home), "Action: Get Associations"));
				item.addItem(getAssociationItem);

				TreeItem getFoldersItem = new TreeItem(HyperlinkFactory.getFoldersForDocument(tab, new ObjectRef(de.id, de.home), "Action: Get Folders"));
				item.addItem(getFoldersItem);
				
				TreeItem getLogicalItem = new TreeItem(HyperlinkFactory.getDocuments(tab, null, new ObjectRefs(new ObjectRef(de.lid, de.home)), "Action: Get All Versions", true));
				item.addItem(getLogicalItem);

				TreeItem retrieveItem = new TreeItem(HyperlinkFactory.retrieve(tab, de, "Action: Retrieve"));
				item.addItem(retrieveItem);

//				TreeItem editItem = new TreeItem(HyperlinkFactory.edit(tab, de, container, data.siteSpec));
//				item.addItem(editItem);
			}

			root.addItem(item);
		}

		for (Association a : data.combinedMetadata.assocs) {
			TreeItem item = new TreeItem(HyperlinkFactory.link(tab, a));
			root.addItem(item);
		}

		TreeItem orItem = null;
		if (data.combinedMetadata.objectRefs.size() > 0) {
			orItem = new TreeItem(Integer.toString(data.combinedMetadata.objectRefs.size()) + " ObjectRefs");
			root.addItem(orItem);

			for (ObjectRef o : data.combinedMetadata.objectRefs) {
				TreeItem item = new TreeItem(HyperlinkFactory.link(tab, o));
				orItem.addItem(item);
			}
		}

		if (data.allDocs != null && !data.allDocs.isEmpty()) {
			for (Document doc : data.allDocs) {
				String title = "";
				DocumentEntry de = data.combinedMetadata.getDocumentEntry(doc.uid);
				if (de != null) {
					title = "(" + de.title + ")";
				}

				HTML h = new HTML();
				h.setHTML("<a href=\"" + doc.cacheURL + "\" target=\"_blank\">Document" +  title + " [external]</a>");
				TreeItem item = new TreeItem(h);
				root.addItem(item);
			}
		}

	}


}
