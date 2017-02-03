package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

/**
 * Created by onh2 on 11/14/16.
 */
public class GetOnDemandDocumentEntryDetailsRequest extends CommandContext{
    private SimId simId;

    public GetOnDemandDocumentEntryDetailsRequest(){}
    public GetOnDemandDocumentEntryDetailsRequest(CommandContext context, SimId simId){
        copyFrom(context);
        this.simId=simId;
    }

    public SimId getSimId() {
        return simId;
    }

    public void setSimId(SimId simId) {
        this.simId = simId;
    }
}
