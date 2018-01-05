package gov.nist.toolkit.xdstools2.client.inspector;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.TreeItem;
import gov.nist.toolkit.registrymetadata.client.Association;
import gov.nist.toolkit.registrymetadata.client.Document;
import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.registrymetadata.client.Folder;
import gov.nist.toolkit.registrymetadata.client.MetadataCollection;
import gov.nist.toolkit.registrymetadata.client.ObjectRef;
import gov.nist.toolkit.registrymetadata.client.ObjectRefs;
import gov.nist.toolkit.registrymetadata.client.SubmissionSet;
import gov.nist.toolkit.results.client.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ListingDisplay {
   /** Parent tab for this display */
	MetadataInspectorTab tab;
	/** DataModel for this display, could be test or step */
	DataModel data;
	/** Tree element serving as root for this display */
	TreeThing root;

	Map<String, DataModel> groupByMap = new HashMap<String, DataModel>();

	public ListingDisplay(MetadataInspectorTab tab, DataModel data, TreeThing root) {
		this.tab = tab;
		this.data = data;
		this.root = root;
	}




	/**
	 * Build tree display for a DataModel
	 */
	void listing() {

		groupByMap.clear();

		submissionSets();

		folders();

		documentEntries();

		associations();

		objectRefs();

		allDocs();

		others();

	}

	void allDocs() {
    /*
     * Display links to each document
     */
		if (data.allDocs != null && !data.allDocs.isEmpty()) {
			for (Document doc : data.allDocs) {
				addDoc(root, doc);
			}
		}
	}

	void addDoc(TreeThing treeThing, Document doc) {
		String title = "";
		DocumentEntry de = data.combinedMetadata.getDocumentEntry(doc.uid);
		if (de != null) {
            title = "(" + de.title + ")";
        }

		HTML h = new HTML();
		h.setHTML("<a href=\"" + doc.cacheURL + "\" target=\"_blank\">Document" +  title + " [external]</a>");
		TreeItem item = new TreeItem(h);
		treeThing.addItem(item);
	}

	void objectRefs() {
		if (data.combinedMetadata.objectRefs.size() > 0) {
			TreeItem ti = new TreeItem();
			ti.setHTML(Integer.toString(data.combinedMetadata.objectRefs.size()) + " ObjectRefs");
			root.addItem(ti);

			boolean hasHcIds = false;
			for (ObjectRef o : data.combinedMetadata.objectRefs) {
				Hyperlink h = HyperlinkFactory.link(tab, o);
				TreeItem item = new TreeItem(h);
				item.setUserObject(new MetadataObjectWrapper(MetadataObjectType.ObjectRefs,o));
				ti.addItem(item);
			}
		}
	}

	void others() {
		if (data.combinedMetadata.others.size() > 0) {
			TreeItem ti = new TreeItem();
			ti.setHTML(Integer.toString(data.combinedMetadata.others.size()) + " Resources");
			root.addItem(ti);

			for (String o : data.combinedMetadata.others) {
				Hyperlink h = HyperlinkFactory.linkMainXMLView(tab, "Resource", o);
				TreeItem item = new TreeItem(h);
				item.setUserObject(null);
				ti.addItem(item);
			}
		}
	}

	void associations() {
		for (Association a : data.combinedMetadata.assocs) {
			Hyperlink h = HyperlinkFactory.link(tab, a);
			TreeItem item = new TreeItem(h);
			item.setUserObject(new MetadataObjectWrapper(MetadataObjectType.Assocs,a));
			root.addItem(item);
		}
	}

	void documentEntries() {
		for (DocumentEntry de : data.combinedMetadata.docEntries) {
			addDe(root, de);
		}
	}

	void addDe(TreeThing treeThing, DocumentEntry de) {
		Hyperlink h = HyperlinkFactory.link(tab, de);
		TreeItem item = new TreeItem(h);
		item.setUserObject(new MetadataObjectWrapper(MetadataObjectType.DocEntries,de));

		if (data.enableActions) {
			if (!de.isFhir) {
				TreeItem getRelatedItem = new TreeItem(HyperlinkFactory.getRelated(tab, new ObjectRef(de.id, de.home), "Action: Get Related Documents"));
				item.addItem(getRelatedItem);

				TreeItem getSubmissionSetItem = new TreeItem(HyperlinkFactory.getSubmissionSets(tab, new ObjectRef(de.id, de.home), "Action: Get Submission Set"));
				item.addItem(getSubmissionSetItem);

				TreeItem getAssociationItem = new TreeItem(HyperlinkFactory.getAssociations(tab, new ObjectRef(de.id, de.home), "Action: Get Associations"));
				item.addItem(getAssociationItem);

				TreeItem getFoldersItem = new TreeItem(HyperlinkFactory.getFoldersForDocument(tab, new ObjectRef(de.id, de.home), "Action: Get Folders"));
				item.addItem(getFoldersItem);

				TreeItem getLogicalItem = new TreeItem(HyperlinkFactory.getDocuments(tab, null, new ObjectRefs(new ObjectRef(de.lid, de.home)), "Action: Get All Versions", true, data.siteSpec));
				item.addItem(getLogicalItem);
			}
            TreeItem retrieveItem = new TreeItem(HyperlinkFactory.retrieve(tab, de, "Action: Retrieve"));
            item.addItem(retrieveItem);

//				TreeItem editItem = new TreeItem(HyperlinkFactory.edit(tab, de, container, data.siteSpec));
//				item.addItem(editItem);
        }

		root.addItem(item);
	}

	void folders() {
		for (Folder fol : data.combinedMetadata.folders) {
			Hyperlink h = HyperlinkFactory.link(tab, fol);
			TreeItem item = new TreeItem(h);
			item.setUserObject(new MetadataObjectWrapper(MetadataObjectType.Folders,fol));

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
	}

	void submissionSets() {
		for (SubmissionSet ss : data.combinedMetadata.submissionSets) {
			Hyperlink h = HyperlinkFactory.link(tab, ss);
			TreeItem item = new TreeItem(h);
			item.setUserObject(new MetadataObjectWrapper(MetadataObjectType.SubmissionSets,ss));
			root.addItem(item);
		}
	}


	static DataModel newDataModel(List<Result> results) {
		DataModel dm = new DataModel();
		dm.results = results;

		dm.combinedMetadata = new MetadataCollection();
		dm.combinedMetadata.init();

		dm.docMap = new HashMap<String, Document>();
		dm.allDocs = new ArrayList<Document>();
		return dm;
	}


}
