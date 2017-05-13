package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.request.GetSubmissionSetAndContentsRequest;

import java.util.List;

/**
 * Created by onh2 on 10/31/16.
 */
public abstract class GetSubmissionSetAndContentsCommand extends GenericCommand<GetSubmissionSetAndContentsRequest,List<Result>>{
    @Override
    public void run(GetSubmissionSetAndContentsRequest request) {
        XdsTools2Presenter.data().getToolkitServices().getSSandContents(request,this);
    }
}
