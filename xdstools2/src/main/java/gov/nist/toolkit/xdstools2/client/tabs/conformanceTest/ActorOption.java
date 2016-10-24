package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.user.client.rpc.AsyncCallback;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;

import java.util.List;

/**
 * A type that takes into account both the actor type and option selected
 */
public class ActorOption {
    String actorTypeId;
    String optionId;

    public ActorOption(String actorTypeId) {
        this.actorTypeId = actorTypeId;
        optionId = "";
    }

    public ActorOption(String actorTypeId, String optionId) {
        this.actorTypeId = actorTypeId;
        this.optionId = optionId;
    }

    /**
     * Tests for options are listed in collections as actorType_optionName
     * @param callback with list of testIds
     */
    void loadTests(AsyncCallback<List<TestInstance>> callback) {
        if (optionId == null || optionId.equals("")) {
            ClientUtils.INSTANCE.getToolkitServices().getCollectionMembers("actorcollections", actorTypeId, callback);
        } else {
            ClientUtils.INSTANCE.getToolkitServices().getCollectionMembers("collections", actorTypeId + "_" + optionId, callback);
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

    private boolean isInitiatingImagingGatewaySut() {
        return actorTypeId != null
                && ActorType.INITIATING_IMAGING_GATEWAY.getShortName().equals(actorTypeId);
    }

    private boolean isRespondingingImagingGatewaySut() {
        return actorTypeId != null
                && ActorType.RESPONDING_IMAGING_GATEWAY.getShortName().equals(actorTypeId);
    }

    private boolean isEdgeServerSut() {
        return false;
    }

    private boolean isImagingDocSourceSut() {
        return actorTypeId != null
                && ActorType.IMAGING_DOC_SOURCE.getShortName().equals(actorTypeId);
    }

    void setOptionId(String optionId) {
        this.optionId = optionId;
    }

    public String getActorTypeId() {
        return actorTypeId;
    }

    public String getOptionId() {
        return optionId;
    }

    @Override
    public String toString() {
        return "ActorOption: actorType=" + actorTypeId + " option=" + optionId;
    }
}
