package gov.nist.toolkit.xdstools2.client.inspector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import gov.nist.toolkit.registrymetadata.client.AnyId;
import gov.nist.toolkit.registrymetadata.client.AnyIds;
import gov.nist.toolkit.registrymetadata.client.ObjectRef;
import gov.nist.toolkit.registrymetadata.client.ObjectRefs;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.StepResult;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.command.command.GetDocumentsCommand;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.util.SimpleCallback;
import gov.nist.toolkit.xdstools2.shared.command.request.GetDocumentsRequest;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class GetDocuments implements ClickHandler {
	MetadataInspectorTab it;
	StepResult originatingResult = null;
	ObjectRefs ids;
	boolean isLid = false;
	private SiteSpec siteSpec;
	Map<String, AnyIds> byHome;
	Iterator<String> iter;
	boolean preLoadLogs = false;


	void run(AnyIds anyIds) {

		/*it.data.*/
				new GetDocumentsCommand(){
					@Override
					public void onFailure(Throwable caught) {
						Result result = Result.RESULT(new TestInstance("GetDocuments"));
						result.assertions.add(caught.getMessage());
						it.addToHistory(result);
					}
					@Override
					public void onComplete(List<Result> results) {
						for (Result result : results) {
							it.addToHistory(result);

							if (preLoadLogs) {
								RawLogLoader ll = new RawLogLoader(it, result.logId, result.stepResults, new SimpleCallback() {
									@Override
									public void run() {
										if (iter!=null && iter.hasNext()) {
											GetDocuments.this.run(byHome.get(iter.next()));
										}
									}
								});
								ll.loadTestLogs(); // This will load logs and run the next GetDocs for the next AnyIds in the map iterator
							}
							else if (iter.hasNext()) {
								GetDocuments.this.run(byHome.get(iter.next()));
							}
						}
					}
				}.run(new GetDocumentsRequest(ClientUtils.INSTANCE.getCommandContext(),siteSpec,anyIds));
				if (originatingResult != null)
					originatingResult.rmFromToBeRetrieved(ids);

	}

	public Map<String, AnyIds> organizeByHome(AnyIds ids) {
		Map<String, AnyIds> perHome = new HashMap<String, AnyIds>();

		for (AnyId or : ids.ids) {
			String home = or.home;
			if (home == null)
				home = "";
			// add it to the collection
			AnyIds homeOr = perHome.get(home);
			if (homeOr == null) {
				homeOr = new AnyIds();
				perHome.put(home, homeOr);
			}
			homeOr.ids.add(or);
		}

		return perHome;
	}

	public GetDocuments(MetadataInspectorTab it, StepResult originatingResult, ObjectRefs ids, boolean isLid, SiteSpec siteSpec) {
		this.it = it;
		this.originatingResult = originatingResult;
		this.ids = ids;
		this.isLid = isLid;
		this.siteSpec = siteSpec;

		AnyIds aids = new AnyIds(ids);
		if (isLid)
			aids.labelAsLids();

		// This is need to preserve logs and to clean up mixed ids per request with HomeId present in the stored query.
		byHome = organizeByHome(aids);
		iter = byHome.keySet().iterator();
		preLoadLogs = byHome.size()>1;
	}

	public GetDocuments(MetadataInspectorTab it, ObjectRef id, boolean isLid) {
		this.it = it;
		this.ids = new ObjectRefs(id);
		AnyIds aids = new AnyIds(ids);
		this.isLid = isLid;
		byHome = organizeByHome(aids);
		iter = byHome.keySet().iterator();
		preLoadLogs = byHome.size()>1;
	}

	public void onClick(ClickEvent event) {
		if (iter!=null && iter.hasNext())
			run(byHome.get(iter.next()));
	}


}
