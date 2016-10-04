package gov.nist.toolkit.xdstools2.shared.command;

/**
 *
 */
public class GetTestSessionNamesRequest extends CommandContext {

    public GetTestSessionNamesRequest() {}

    public GetTestSessionNamesRequest(CommandContext commandContext) {
        copyFrom(commandContext);
    }
}
