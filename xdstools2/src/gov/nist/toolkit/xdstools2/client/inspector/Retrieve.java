package gov.nist.toolkit.xdstools2.client.inspector;

import gov.nist.toolkit.registrymetadata.client.Uid;
import gov.nist.toolkit.registrymetadata.client.Uids;
import gov.nist.toolkit.results.client.Result;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Retrieve implements ClickHandler {
	MetadataInspectorTab it;
	Uids uids;

	 void run() {
			it.data.toolkitService.retrieveDocument(null, uids, queryCallback);    
		}

		AsyncCallback<List<Result>> queryCallback = new AsyncCallback<List<Result>> () {

			public void onFailure(Throwable caught) {
				Result result = Result.RESULT("Retrieve");
				result.assertions.add(caught.getMessage(), false);
				it.addToHistory(result);
			}

			public void onSuccess(List<Result> result) {
				it.addToHistory(result);
			}

		};
		
		public Retrieve(MetadataInspectorTab it, Uid docUid) {
			this.it = it;
			uids = new Uids();
			uids.uids.add(docUid);
		}


	public void onClick(ClickEvent event) {
		run();
	}

}
