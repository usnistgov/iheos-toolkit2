package gov.nist.toolkit.xdstools2.client.inspector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import gov.nist.toolkit.registrymetadata.client.AnyIds;
import gov.nist.toolkit.registrymetadata.client.ObjectRef;
import gov.nist.toolkit.registrymetadata.client.ObjectRefs;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.xdstools2.client.command.command.GetFoldersForDocumentCommand;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.GetFoldersRequest;

import java.util.List;


public class GetFoldersForDocument implements ClickHandler {
	MetadataInspectorTab it;
	ObjectRefs ids;
	
	void run() {
		/*it.data.*/
		new GetFoldersForDocumentCommand(){
			@Override
			public void onFailure(Throwable caught) {
				Result result = Result.RESULT(new TestInstance("GetAssociations"));
				result.assertions.add(caught.getMessage());
				result.testInstance = new TestInstance("GetAssociations");
				it.addToHistory(result);
			}
			@Override
			public void onComplete(List<Result> results) {
				for (Result result : results) {
					it.addToHistory(result);
				}
			}
		}.run(new GetFoldersRequest(ClientUtils.INSTANCE.getCommandContext(),it.getSiteSpec(),new AnyIds(ids)));
	}

	public GetFoldersForDocument(MetadataInspectorTab it, ObjectRefs ids) {
		this.it = it;
		this.ids = ids;
	}

	public GetFoldersForDocument(MetadataInspectorTab it, ObjectRef id) {
		this.it = it;
		ids = new ObjectRefs();
		ids.objectRefs.add(id);
	}

	public void onClick(ClickEvent event) {
		run();
	}

}
