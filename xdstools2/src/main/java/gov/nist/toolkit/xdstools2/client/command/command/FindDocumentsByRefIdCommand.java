package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.request.FindDocumentsRequest;

import java.util.List;

/**
 * Created by onh2 on 10/31/16.
 */
public abstract class FindDocumentsByRefIdCommand extends GenericCommand<FindDocumentsRequest,List<Result>>{
    @Override
    public void run(FindDocumentsRequest request) {
        XdsTools2Presenter.data().getToolkitServices().findDocumentsByRefId(request,this);
    }
}
