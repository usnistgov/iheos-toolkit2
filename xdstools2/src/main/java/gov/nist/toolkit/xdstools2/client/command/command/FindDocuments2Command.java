package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.request.FindDocuments2Request;

import java.util.List;

/**
 * Created by onh2 on 11/14/16.
 */
public abstract class FindDocuments2Command extends GenericCommand<FindDocuments2Request,List<Result>>{
    @Override
    public void run(FindDocuments2Request var1) {
        XdsTools2Presenter.data().getToolkitServices().findDocuments2(var1,this);
    }
}
