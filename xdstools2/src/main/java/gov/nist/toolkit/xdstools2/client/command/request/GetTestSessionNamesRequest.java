package gov.nist.toolkit.xdstools2.client.command.request;

import gov.nist.toolkit.xdstools2.client.command.CommandContext;

/**
 *
 */
public class GetTestSessionNamesRequest extends CommandContext {

    public GetTestSessionNamesRequest() {}

    public GetTestSessionNamesRequest(CommandContext commandContext) {
        copyFrom(commandContext);
    }
}
