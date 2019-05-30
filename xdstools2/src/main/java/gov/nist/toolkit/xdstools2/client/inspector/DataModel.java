package gov.nist.toolkit.xdstools2.client.inspector;

import gov.nist.toolkit.registrymetadata.client.Document;
import gov.nist.toolkit.registrymetadata.client.MetadataCollection;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.StepResult;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Data for test results. used in ListingDispley and MetadataInspector.
 */
public class DataModel {
	// loaded content organized two ways:
	//    results - organized as a list - shows history of queries
	//    combinedMetadata - raw contents loaded in inspector
   
   // One Result for each Test
	List<Result> results = null;
	MetadataCollection combinedMetadata = null;

	SiteSpec siteSpec = null;
	
//	ToolkitServiceAsync toolkitService = null;

	List<Document> allDocs = null;
	Map<String, Document> docMap = null;  //key is uid
	
	boolean enableActions = true;
	
	public DataModel() {}
	
	public DataModel(DataModel data) {
		results = data.results;
		combinedMetadata = data.combinedMetadata;
		siteSpec = data.siteSpec;
//		toolkitService = data.toolkitService;
		allDocs = data.allDocs;
		docMap = data.docMap;
		enableActions = data.enableActions;
	}
	
	
	// content is loaded into results one query at a time
	// this builds the combined structure
	public void buildCombined(MetadataCollection mc) {
		combinedMetadata = new MetadataCollection();
		combinedMetadata.init();

		docMap = new HashMap<String, Document>();

		try {
		    if (results!=null) {
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
			} else if (mc!=null) {
				combinedMetadata.add(mc);
			}
		} catch (Exception e) {}

		allDocs = new ArrayList<Document>();
		if (docMap.values() != null)
			allDocs.addAll(docMap.values());
	}

	public List<Result> getResults() {
		return results;
	}

	public void setResults(List<Result> results) {
		this.results = results;
	}

	public MetadataCollection getCombinedMetadata() {
		return combinedMetadata;
	}

	public SiteSpec getSiteSpec() {
		return siteSpec;
	}

	public void setSiteSpec(SiteSpec siteSpec) {
		this.siteSpec = siteSpec;
	}

	public List<Document> getAllDocs() {
		return allDocs;
	}


	public Map<String, Document> getDocMap() {
		return docMap;
	}


	public boolean isEnableActions() {
		return enableActions;
	}

}