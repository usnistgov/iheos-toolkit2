package gov.nist.toolkit.xdstools2.client.inspector;

import gov.nist.toolkit.registrymetadata.client.ObjectRef;
import gov.nist.toolkit.registrymetadata.client.ObjectRefs;
import gov.nist.toolkit.results.client.Result;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GetObjects implements ClickHandler {
	MetadataInspectorTab it;
	ObjectRefs ids;
	
	void run() {
		it.data.toolkitService.getObjects(null, ids, queryCallback);
	}
	
	AsyncCallback<List<Result>> queryCallback = new AsyncCallback<List<Result>> () {

		public void onFailure(Throwable caught) {
			Result result = Result.RESULT("GetObjects");
			result.assertions.add(caught.getMessage());
			it.addToHistory(result);
		}

		public void onSuccess(List<Result> results) {
			for (Result result : results) {
				it.addToHistory(result);
			}
		}

	};

	public GetObjects(MetadataInspectorTab it, ObjectRefs ids) {
		this.it = it;
		this.ids = ids;
	}

	public GetObjects(MetadataInspectorTab it, ObjectRef id) {
		this.it = it;
		ids = new ObjectRefs();
		ids.objectRefs.add(id);
	}

	public void onClick(ClickEvent event) {
		run();
	}

}
