package gov.nist.toolkit.xdstools2.client.inspector;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TreeItem;
import gov.nist.toolkit.registrymetadata.client.Document;
import gov.nist.toolkit.registrymetadata.client.DocumentEntry;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class RepositoryIdListingDisplay extends ListingDisplay {

	public RepositoryIdListingDisplay(MetadataInspectorTab tab, DataModel data, TreeThing root) {
		super(tab, data, root, null, null);
	}

	/**
	 * Build tree display for a DataModel
	 */

	@Override
	void documentEntries() {
		boolean hasRepositoryIds = false;

		for (DocumentEntry de : data.combinedMetadata.docEntries) {
			if (de.repositoryUniqueId != null && !"".equals(de.repositoryUniqueId)) {
				hasRepositoryIds = true;
				DataModel dm = getGroupIdCollection(de.repositoryUniqueId);
				dm.combinedMetadata.docEntries.add(de);
			} else {
			   addDe(root, de);
            }


		}
		if (hasRepositoryIds) {
			Map<String, DataModel> treeMap = new TreeMap<>(groupByMap);
			for (Map.Entry<String, DataModel> entry : treeMap.entrySet()) {
				String reposId = entry.getKey();

				TreeItem reposTreeItem = new TreeItem(new HTML(reposId));
				root.addItem(reposTreeItem);
				TreeThing treeThing = new TreeThing(reposTreeItem);

				List<DocumentEntry> docList = getGroupIdCollection(reposId).combinedMetadata.docEntries;
				for (DocumentEntry de : docList) {
					addDe(treeThing, de);
				}
			}
		}
	}


	@Override
	void allDocs() {
    /*
     * Display links to each document
     */
		if (data.allDocs != null && !data.allDocs.isEmpty()) {
			boolean hasRepositoryIds = false;

			for (Document doc : data.allDocs) {
			    if (doc.repositoryUniqueId != null && !"".equals(doc.repositoryUniqueId)) {
			    	hasRepositoryIds = true;
			    	DataModel dm = getGroupIdCollection(doc.repositoryUniqueId);
			    	dm.allDocs.add(doc);
				} else {
			        addDoc(root, doc);
				}
			}

			if (hasRepositoryIds) {
				Map<String, DataModel> treeMap = new TreeMap<>(groupByMap);
				for (Map.Entry<String, DataModel> entry : treeMap.entrySet()) {
					String reposId = entry.getKey();

					TreeItem reposTreeItem = new TreeItem(new HTML(reposId));
					root.addItem(reposTreeItem);
					TreeThing treeThing = new TreeThing(reposTreeItem);

					List<Document> docList = getGroupIdCollection(reposId).allDocs;
					for (Document doc : docList) {
						addDoc(treeThing, doc);
					}
				}
			}
		}
	}


	DataModel getGroupIdCollection(String groupById) {
		if (groupByMap.containsKey(groupById))	 {
			return groupByMap.get(groupById);
		} else {
			DataModel dm = ListingDisplay.newDataModel(data.results);
			groupByMap.put(groupById, dm);
			return dm;
		}
	}

}
