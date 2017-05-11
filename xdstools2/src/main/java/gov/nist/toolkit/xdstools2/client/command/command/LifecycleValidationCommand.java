package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.client.initialization.FrameworkInitialization;
import gov.nist.toolkit.xdstools2.shared.command.request.LifecycleValidationRequest;

import java.util.List;

/**
 * Created by onh2 on 11/14/16.
 */
public abstract class LifecycleValidationCommand extends GenericCommand<LifecycleValidationRequest,List<Result>>{
    @Override
    public void run(LifecycleValidationRequest var1) {
        FrameworkInitialization.data().getToolkitServices().lifecycleValidation(var1,this);
    }
}
