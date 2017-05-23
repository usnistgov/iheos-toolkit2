package gov.nist.toolkit.desktop.shared.command.request;


import gov.nist.toolkit.desktop.client.commands.util.CommandContext;

/**
 *
 */
public class GeneratePidRequest extends CommandContext {
    String assigningAuthority;

    public GeneratePidRequest() {
    }

    public GeneratePidRequest(CommandContext commandContext, String assigningAuthority) {
        copyFrom(commandContext);
        this.assigningAuthority = assigningAuthority;
    }

    public String getAssigningAuthority() {
        return assigningAuthority;
    }
}
