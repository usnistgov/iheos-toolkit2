package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

public class GetAdminToolkitPropertiesRequest extends CommandContext {
    String hash;

    public GetAdminToolkitPropertiesRequest() {
    }

    public GetAdminToolkitPropertiesRequest(CommandContext commandContext, String hash) {
        copyFrom(commandContext);
        this.hash = hash;
    }

    public String getHash() {
        return hash;
    }

}
