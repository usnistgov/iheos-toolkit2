package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.request.ProvideAndRetrieveRequest;

import java.util.List;

/**
 * Created by onh2 on 11/14/16.
 */
public abstract class ProvideAndRetrieveCommand extends GenericCommand<ProvideAndRetrieveRequest,List<Result>>{
    @Override
    public void run(ProvideAndRetrieveRequest var1) {
        XdsTools2Presenter.data().getToolkitServices().provideAndRetrieve(var1,this);
    }
}
