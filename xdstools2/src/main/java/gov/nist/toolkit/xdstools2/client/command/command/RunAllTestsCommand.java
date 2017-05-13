package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.results.shared.Test;
import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.shared.command.request.AllTestRequest;

import java.util.List;

/**
 * Created by onh2 on 11/10/16.
 */
public abstract class RunAllTestsCommand extends GenericCommand<AllTestRequest,List<Test>>{
    @Override
    public void run(AllTestRequest var1) {
        XdsTools2Presenter.data().getToolkitServices().runAllTests(var1,this);
    }
}
