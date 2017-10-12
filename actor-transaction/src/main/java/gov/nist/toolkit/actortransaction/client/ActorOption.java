package gov.nist.toolkit.actortransaction.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

public class ActorOption implements Serializable, IsSerializable {
    public String profileId;
    public String optionId;
    public String actorTypeId;

    public ActorOption() {

    }

    public ActorOption(String actorTypeShortName)  {
        String[] parts = actorTypeShortName.split("_");
        if (parts.length >= 3) {
            profileId = parts[0];
            actorTypeId = parts[1];
            optionId = parts[2];
        } else if (parts.length == 2) {
            profileId = "xds";
            actorTypeId = parts[0];
            optionId = parts[1];
        } else if (parts.length == 1) {
            profileId = "xds";
            actorTypeId = parts[0];
            optionId = "";
        }
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

    public void copyFrom(ActorOption ao) {
        profileId = ao.profileId;
        optionId = ao.optionId;
        actorTypeId = ao.actorTypeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ActorOption that = (ActorOption) o;

        if (profileId != null ? !profileId.equals(that.profileId) : that.profileId != null) return false;
        if (optionId != null ? !optionId.equals(that.optionId) : that.optionId != null) return false;
        return actorTypeId != null ? actorTypeId.equals(that.actorTypeId) : that.actorTypeId == null;
    }

    @Override
    public int hashCode() {
        int result = profileId != null ? profileId.hashCode() : 0;
        result = 31 * result + (optionId != null ? optionId.hashCode() : 0);
        result = 31 * result + (actorTypeId != null ? actorTypeId.hashCode() : 0);
        return result;
    }
}
