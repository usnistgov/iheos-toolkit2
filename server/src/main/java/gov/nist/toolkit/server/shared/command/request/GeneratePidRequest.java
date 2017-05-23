package gov.nist.toolkit.server.shared.command.request;


import gov.nist.toolkit.server.shared.command.CommandContext;

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
