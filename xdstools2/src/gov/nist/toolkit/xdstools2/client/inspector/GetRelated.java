package gov.nist.toolkit.xdstools2.client.inspector;

import gov.nist.toolkit.registrymetadata.client.ObjectRef;
import gov.nist.toolkit.results.client.Result;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GetRelated implements ClickHandler {
	MetadataInspectorTab it;
	ObjectRef or;
	List<String> assocs;
	
	public GetRelated(MetadataInspectorTab it, ObjectRef or, List<String> assocs) {
		this.it = it;
		this.or = or;
		this.assocs = assocs;
	}
	
	AsyncCallback<List<Result>> queryCallback = new AsyncCallback<List<Result>> () {

		public void onFailure(Throwable caught) {
			Result result = Result.RESULT("GetRelated");
			result.assertions.add(caught.getMessage());
			it.addToHistory(result);
		}

		public void onSuccess(List<Result> result) {
			it.addToHistory(result);
		}

	};
	
	void run() {
		it.data.toolkitService.getRelated(null, or, assocs, queryCallback);
	}
	
	public void onClick(ClickEvent event) {
		run();
	}

}
