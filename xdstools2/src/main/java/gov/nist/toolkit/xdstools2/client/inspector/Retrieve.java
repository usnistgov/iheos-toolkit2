package gov.nist.toolkit.xdstools2.client.inspector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import gov.nist.toolkit.registrymetadata.client.Uid;
import gov.nist.toolkit.registrymetadata.client.Uids;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.xdstools2.client.command.command.RetrieveDocumentCommand;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.RetrieveDocumentRequest;

import java.util.List;


public class Retrieve implements ClickHandler {
	MetadataInspectorTab it;
	Uids uids;

	void run() {
			/*it.data.*/
		new RetrieveDocumentCommand(){
			@Override
			public void onFailure(Throwable caught) {
				Result result = Result.RESULT(new TestInstance("Retrieve"));
				result.assertions.add(caught.getMessage(), false);
				it.addToHistory(result);
			}
			@Override
			public void onComplete(List<Result> result) {
				it.addToHistory(result);
			}
		}.run(new RetrieveDocumentRequest(ClientUtils.INSTANCE.getCommandContext(),null ,uids));
	}

	public Retrieve(MetadataInspectorTab it, Uid docUid) {
		this.it = it;
		uids = new Uids();
		uids.uids.add(docUid);
	}


	public void onClick(ClickEvent event) {
		run();
	}

}
