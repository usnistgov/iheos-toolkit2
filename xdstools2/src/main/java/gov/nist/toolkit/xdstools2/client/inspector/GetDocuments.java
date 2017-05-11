package gov.nist.toolkit.xdstools2.client.inspector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import gov.nist.toolkit.registrymetadata.client.AnyIds;
import gov.nist.toolkit.registrymetadata.client.ObjectRef;
import gov.nist.toolkit.registrymetadata.client.ObjectRefs;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.StepResult;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.xdstools2.client.command.command.GetDocumentsCommand;
import gov.nist.toolkit.xdstools2.client.initialization.FrameworkInitialization;
import gov.nist.toolkit.xdstools2.shared.command.request.GetDocumentsRequest;

import java.util.List;


public class GetDocuments implements ClickHandler {
	MetadataInspectorTab it;
	StepResult originatingResult = null;
	ObjectRefs ids;
	boolean isLid = false;

	void run() {
		AnyIds aids = new AnyIds(ids);
		if (isLid)
			aids.labelAsLids();
		
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
				}
			}
		}.run(new GetDocumentsRequest(FrameworkInitialization.data().getCommandContext(),null,aids));
		if (originatingResult != null)
			originatingResult.rmFromToBeRetrieved(ids);
	}

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
