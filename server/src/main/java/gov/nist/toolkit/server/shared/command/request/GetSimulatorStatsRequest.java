package gov.nist.toolkit.server.shared.command.request;

import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.server.shared.command.CommandContext;

import java.util.List;

/**
 * Created by onh2 on 11/7/16.
 */
public class GetSimulatorStatsRequest extends CommandContext {
    private List<SimId> simid;

    public GetSimulatorStatsRequest(){}
    public GetSimulatorStatsRequest(CommandContext context, List<SimId> simid){
        copyFrom(context);
        this.simid=simid;
    }

    public List<SimId> getSimid() {
        return simid;
    }

    public void setSimid(List<SimId> simid) {
        this.simid = simid;
    }
}
