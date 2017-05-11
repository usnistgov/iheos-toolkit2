package gov.nist.toolkit.xdstools2.client.command.command;

import gov.nist.toolkit.valsupport.client.MessageValidationResults;
import gov.nist.toolkit.xdstools2.client.initialization.FrameworkInitialization;
import gov.nist.toolkit.xdstools2.shared.command.request.ValidateMessageRequest;

/**
 * Created by onh2 on 10/21/16.
 */
public abstract class ValidateMessageCommand extends GenericCommand<ValidateMessageRequest,MessageValidationResults>{
    @Override
    public void run(ValidateMessageRequest request) {
        FrameworkInitialization.data().getToolkitServices().validateMessage(request,this);
    }
}
