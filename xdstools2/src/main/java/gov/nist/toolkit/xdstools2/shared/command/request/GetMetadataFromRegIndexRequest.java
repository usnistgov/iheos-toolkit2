package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 *
 */
public class GetMetadataFromRegIndexRequest extends CommandContext {
    private SimId simId;

    public GetMetadataFromRegIndexRequest(CommandContext commandContext, SimId simId) {
        copyFrom(commandContext);
        this.simId = simId;
    }

    public GetMetadataFromRegIndexRequest() {

    }

    public SimId getSimId() {
        return simId;
    }

    public void setSimId(SimId simId) {
        this.simId = simId;
    }
}
