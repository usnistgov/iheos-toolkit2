package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTestResultsRequest;

import java.util.Map;

/**
 * Created by onh2 on 11/10/16.
 */
public abstract class GetTestResultsCommand extends GenericCommand<GetTestResultsRequest,Map<String,Result>>{
    @Override
    public void run(GetTestResultsRequest var1) {
        XdsTools2Presenter.data().getToolkitServices().getTestResults(var1,this);
    }
}
