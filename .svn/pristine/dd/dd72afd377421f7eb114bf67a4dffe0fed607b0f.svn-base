package gov.nist.toolkit.xdstools2.client.inspector;

import gov.nist.toolkit.registrymetadata.client.Document;
import gov.nist.toolkit.registrymetadata.client.MetadataCollection;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.results.client.StepResult;
import gov.nist.toolkit.xdstools2.client.ToolkitServiceAsync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataModel {
	// loaded content organized two ways:
	//    results - organized as a list - shows history of queries
	//    combinedMetadata - raw contents loaded in inspector
	List<Result> results = null;
	MetadataCollection combinedMetadata = null;

	SiteSpec siteSpec = null;
	
	ToolkitServiceAsync toolkitService = null;

	List<Document> allDocs = null;
	Map<String, Document> docMap = null;  //key is uid
	
	boolean enableActions = true;
	
	public DataModel() {}
	
	public DataModel(DataModel data) {
		results = data.results;
		combinedMetadata = data.combinedMetadata;
		siteSpec = data.siteSpec;
		toolkitService = data.toolkitService;
		allDocs = data.allDocs;
		docMap = data.docMap;
		enableActions = data.enableActions;
	}
	
	
	// content is loaded into results one query at a time
	// this builds the combined structure
	void buildCombined() {
		combinedMetadata = new MetadataCollection();
		combinedMetadata.init();

		docMap = new HashMap<String, Document>();

		try {
			for (Result result : results) {
				for (StepResult stepResult : result.stepResults) {
					combinedMetadata.add(stepResult.getMetadata());
					if (stepResult.documents != null) {
						for (Document doc : stepResult.documents) {
							docMap.put(doc.uid, doc);
						}
					}
				}
			}
		} catch (Exception e) {}

		allDocs = new ArrayList<Document>();
		if (docMap.values() != null)
			allDocs.addAll(docMap.values());


	}

}