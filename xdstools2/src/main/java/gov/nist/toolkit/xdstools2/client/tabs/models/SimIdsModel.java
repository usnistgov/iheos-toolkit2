package gov.nist.toolkit.xdstools2.client.tabs.models;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.simcommon.client.SimId;

import java.io.Serializable;
import java.util.List;

/**
 *
 */
public class SimIdsModel implements Serializable, IsSerializable {
    private List<SimId> simIds;

    public SimIdsModel() {}

    public List<SimId> getSimIds() {
        return simIds;
    }

    public void setSimIds(List<SimId> simIds) {
        this.simIds = simIds;
    }
}
