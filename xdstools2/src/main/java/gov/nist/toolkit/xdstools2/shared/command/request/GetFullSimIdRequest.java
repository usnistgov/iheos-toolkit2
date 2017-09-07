package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 *
 */
public class GetFullSimIdRequest extends CommandContext {
    private SimId simId;

    public GetFullSimIdRequest(CommandContext commandContext, SimId simId) {
        copyFrom(commandContext);
        this.simId = simId;
    }

    public GetFullSimIdRequest() {

    }

    public SimId getSimId() {
        return simId;
    }

    public void setSimId(SimId simId) {
        this.simId = simId;
    }
}
