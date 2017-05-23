package gov.nist.toolkit.desktop.shared.command.request;


import gov.nist.toolkit.desktop.client.commands.util.CommandContext;

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
