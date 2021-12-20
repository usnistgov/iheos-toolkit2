package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

public class GetAdminPasswordHashRequest extends CommandContext {
    private String password;

    public GetAdminPasswordHashRequest(CommandContext commandContext, String password) {
        copyFrom(commandContext);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
