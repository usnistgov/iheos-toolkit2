package gov.nist.toolkit.desktop.shared.command.request;

import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.desktop.client.commands.util.CommandContext;

import java.util.List;

/**
 * Created by onh2 on 11/7/16.
 */
public class PatientIdsRequest extends CommandContext {
    private List<Pid> pids;
    private SimId simid;

    public PatientIdsRequest(){}
    public PatientIdsRequest(CommandContext context, SimId simId){
        copyFrom(context);
        this.simid=simId;
    }
    public PatientIdsRequest(CommandContext context, SimId simId, List<Pid> pids){
        this(context,simId);
        this.pids=pids;
    }

    public List<Pid> getPids() {
        return pids;
    }

    public void setPids(List<Pid> pids) {
        this.pids = pids;
    }

    public SimId getSimId() {
        return simid;
    }

    public void setSimid(SimId simid) {
        this.simid = simid;
    }
}
