package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.valsupport.client.MessageValidationResults;
import gov.nist.toolkit.xdstools2.client.initialization.FrameworkInitialization;
import gov.nist.toolkit.xdstools2.shared.command.request.ExecuteSimMessageRequest;

/**
 * Created by onh2 on 10/21/16.
 */
public abstract class ExecuteSimMessageCommand extends GenericCommand<ExecuteSimMessageRequest,MessageValidationResults>{
    @Override
    public void run(ExecuteSimMessageRequest request) {
        FrameworkInitialization.data().getToolkitServices().executeSimMessage(request,this);
    }
}
