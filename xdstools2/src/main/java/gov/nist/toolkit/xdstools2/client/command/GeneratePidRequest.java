package gov.nist.toolkit.xdstools2.client.command;

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
