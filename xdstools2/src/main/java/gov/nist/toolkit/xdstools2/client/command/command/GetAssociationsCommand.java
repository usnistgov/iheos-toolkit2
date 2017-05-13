package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.request.GetAssociationsRequest;

import java.util.List;

/**
 * Created by onh2 on 11/3/16.
 */
public abstract class GetAssociationsCommand extends GenericCommand<GetAssociationsRequest,List<Result>>{
    @Override
    public void run(GetAssociationsRequest var1) {
        XdsTools2Presenter.data().getToolkitServices().getAssociations(var1,this);
    }
}
