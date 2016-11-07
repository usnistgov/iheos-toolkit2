package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

import java.util.List;

/**
 * Created by onh2 on 11/7/16.
 */
public class GetSimConfigsRequest extends CommandContext{
    private List<SimId> ids;

    public GetSimConfigsRequest(){}
    public GetSimConfigsRequest(CommandContext context,List<SimId> ids){
        copyFrom(context);
        this.ids=ids;
    }

    public List<SimId> getIds() {
        return ids;
    }

    public void setIds(List<SimId> ids) {
        this.ids = ids;
    }
}
