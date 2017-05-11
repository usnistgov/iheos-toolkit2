package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.client.initialization.FrameworkInitialization;
import gov.nist.toolkit.xdstools2.shared.command.request.RegisterAndQueryRequest;

import java.util.List;

/**
 * Created by onh2 on 11/4/16.
 */
public abstract class RegisterAndQueryCommand extends GenericCommand<RegisterAndQueryRequest,List<Result>>{
    @Override
    public void run(RegisterAndQueryRequest var1) {
        FrameworkInitialization.data().getToolkitServices().registerAndQuery(var1,this);
    }
}
