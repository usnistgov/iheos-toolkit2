package gov.nist.toolkit.xdstools2.shared.command;

/**
 *
 */
public class GetAllSimConfigsRequest extends CommandContext {
    String user;

    public GetAllSimConfigsRequest() {}

    public GetAllSimConfigsRequest(CommandContext commandContext, String user) {
        copyFrom(commandContext);
        this.user = user;
    }

    public String getUser() {
        return user;
    }
}
