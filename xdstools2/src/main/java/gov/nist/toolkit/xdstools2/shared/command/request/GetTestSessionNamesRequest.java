package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 *
 */
public class GetTestSessionNamesRequest extends CommandContext {

    public GetTestSessionNamesRequest() {}

    public GetTestSessionNamesRequest(CommandContext commandContext) {
        copyFrom(commandContext);
    }
}
