package gov.nist.toolkit.xdstools2.client.inspector;

import gov.nist.toolkit.registrymetadata.client.AnyIds;
import gov.nist.toolkit.registrymetadata.client.ObjectRef;
import gov.nist.toolkit.registrymetadata.client.ObjectRefs;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.StepResult;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GetDocuments implements ClickHandler {
	MetadataInspectorTab it;
	StepResult originatingResult = null;
	ObjectRefs ids;
	boolean isLid = false;

	void run() {
		AnyIds aids = new AnyIds(ids);
		if (isLid)
			aids.labelAsLids();
		
		it.data.toolkitService.getDocuments(null, aids, queryCallback);
		if (originatingResult != null)
			originatingResult.rmFromToBeRetrieved(ids);
	}

	AsyncCallback<List<Result>> queryCallback = new AsyncCallback<List<Result>> () {

		public void onFailure(Throwable caught) {
			Result result = Result.RESULT("GetDocuments");
			result.assertions.add(caught.getMessage());
			it.addToHistory(result);
		}

		public void onSuccess(List<Result> results) {
			for (Result result : results) {
				it.addToHistory(result);
			}
		}

	};

	public GetDocuments(MetadataInspectorTab it, StepResult originatingResult, ObjectRefs ids, boolean isLid) {
		this.it = it;
		this.originatingResult = originatingResult;
		this.ids = ids;
		this.isLid = isLid;
	}

	public GetDocuments(MetadataInspectorTab it, ObjectRef id, boolean isLid) {
		this.it = it;
		this.ids = new ObjectRefs(id);
		this.isLid = isLid;
	}

	public void onClick(ClickEvent event) {
		run();
	}


}
