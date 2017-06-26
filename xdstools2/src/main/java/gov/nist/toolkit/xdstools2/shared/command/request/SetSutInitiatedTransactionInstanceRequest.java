package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.interactionmodel.client.InteractingEntity;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

import java.util.List;

/**
 * Created by skb1 on 3/15/17.
 */
public class SetSutInitiatedTransactionInstanceRequest extends CommandContext{
    List<InteractingEntity> interactingEntityList;
    SimId tranDestinationSimId;
    String patienId;


    public SetSutInitiatedTransactionInstanceRequest(){}
    public SetSutInitiatedTransactionInstanceRequest(CommandContext context, List<InteractingEntity> interactingEntityList, SimId tranDestinationSimId, String patienId){
        copyFrom(context);
        setInteractingEntityList(interactingEntityList);
        setTranDestinationSimId(tranDestinationSimId);
        setPatienId(patienId);
    }


    public List<InteractingEntity> getInteractingEntityList() {
        return interactingEntityList;
    }

    public void setInteractingEntityList(List<InteractingEntity> interactingEntityList) {
        this.interactingEntityList = interactingEntityList;
    }

    public SimId getTranDestinationSimId() {
        return tranDestinationSimId;
    }

    public void setTranDestinationSimId(SimId tranDestinationSimId) {
        this.tranDestinationSimId = tranDestinationSimId;
    }

    public String getPatienId() {
        return patienId;
    }

    public void setPatienId(String patienId) {
        this.patienId = patienId;
    }
}
