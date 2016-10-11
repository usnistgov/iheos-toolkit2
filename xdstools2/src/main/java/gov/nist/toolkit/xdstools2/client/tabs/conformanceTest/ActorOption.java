package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.user.client.rpc.AsyncCallback;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;

import java.util.List;

/**
 * A type that takes into account both the actor type and option selected
 */
public class ActorOption {
    String actorTypeId;
    String optionName;

    public ActorOption(String actorTypeId) {
        this.actorTypeId = actorTypeId;
        optionName = "";
    }

    public ActorOption(String actorTypeId, String optionName) {
        this.actorTypeId = actorTypeId;
        this.optionName = optionName;
    }

    /**
     * Tests for options are listed in collections as actorType_optionName
     * @param callback
     */
    public void loadTests(AsyncCallback<List<String>> callback) {
        if (optionName == null || optionName.equals("")) {
            ClientUtils.INSTANCE.getToolkitServices().getCollectionMembers("actorCollections", actorTypeId, callback);
        } else {
            ClientUtils.INSTANCE.getToolkitServices().getCollectionMembers("collections", actorTypeId + "_" + optionName, callback);
        }
    }

    public boolean isRep() {
        return actorTypeId != null && ActorType.REPOSITORY.getShortName().equals(actorTypeId);
    }

    public boolean isRg() {
        return actorTypeId != null && ActorType.RESPONDING_GATEWAY.getShortName().equals(actorTypeId);
    }

    public boolean isIg() {
        return actorTypeId != null && ActorType.INITIATING_GATEWAY.getShortName().equals(actorTypeId);
    }

    public boolean isReg() {
        return actorTypeId != null && ActorType.REGISTRY.getShortName().equals(actorTypeId);
    }
}
