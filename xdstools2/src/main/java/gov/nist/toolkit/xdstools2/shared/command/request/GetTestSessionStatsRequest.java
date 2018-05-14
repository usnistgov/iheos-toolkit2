package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

public class GetTestSessionStatsRequest extends CommandContext {

    public GetTestSessionStatsRequest() {
    }

    public GetTestSessionStatsRequest(CommandContext commandContext) {
        copyFrom(commandContext);
    }
}
