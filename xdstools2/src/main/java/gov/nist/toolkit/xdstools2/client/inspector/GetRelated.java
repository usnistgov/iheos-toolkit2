package gov.nist.toolkit.xdstools2.client.inspector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import gov.nist.toolkit.registrymetadata.client.ObjectRef;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.xdstools2.client.command.command.GetRelatedCommand;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.GetRelatedRequest;

import java.util.List;


public class GetRelated implements ClickHandler {
	MetadataInspectorTab it;
	ObjectRef or;
	List<String> assocs;

	public GetRelated(MetadataInspectorTab it, ObjectRef or, List<String> assocs) {
		this.it = it;
		this.or = or;
		this.assocs = assocs;
	}

	void run() {
		/*it.data.*/
		new GetRelatedCommand(){
			@Override
			public void onFailure(Throwable caught) {
				Result result = Result.RESULT(new TestInstance("GetRelated"));
				result.assertions.add(caught.getMessage());
				it.addToHistory(result);
			}
			@Override
			public void onComplete(List<Result> result) {
				it.addToHistory(result);
			}
		}.run(new GetRelatedRequest(ClientUtils.INSTANCE.getCommandContext(),null,or,assocs));
	}

	public void onClick(ClickEvent event) {
		run();
	}

}
