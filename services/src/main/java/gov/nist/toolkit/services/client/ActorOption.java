package gov.nist.toolkit.services.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

public class ActorOption implements Serializable, IsSerializable {
    public String profileId;
    public String optionId;
    public String actorTypeId;

    public ActorOption() {

    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

    public String getActorTypeId() {
        return actorTypeId;
    }

    public void setActorTypeId(String actorTypeId) {
        this.actorTypeId = actorTypeId;
    }
}
