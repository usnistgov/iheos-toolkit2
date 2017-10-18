package gov.nist.toolkit.actortransaction.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

public class ActorOption implements Serializable, IsSerializable {
    public IheItiProfile profileId;
    public String optionId;
    public String actorTypeId;

    public ActorOption() {

    }

    /**
     * Will parse codes such as:
     * actor        # Required option
     * actor_option
     * actor(profile) # Required
     * actor(profile)_option
     * @param actorTypeShortName
     */
    public ActorOption(String actorTypeShortName)  {
        String[] parts = actorTypeShortName.split("_");

        if (actorTypeShortName.contains("_")) {
            optionId = parts[1];
        } else {
               optionId = "";
         }
        if (actorTypeShortName.contains("(") && actorTypeShortName.contains(")"))  {
            String[] actorProfile = actorTypeShortName.split("\\(");
            actorTypeId = actorProfile[0];
            String profileId = actorProfile[1].replace(")","");
            this.profileId = IheItiProfile.find(profileId);
        } else {
            actorTypeId = parts[0];
            profileId = IheItiProfile.XDS;
        }

    }

    public IheItiProfile getProfileId() {
        return profileId;
    }

    public void setProfileId(IheItiProfile profile) {
        this.profileId = profile;
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

    @Override
    public String toString() {
        return "ActorOption{" +
                "profileId=" + profileId +
                ", optionId='" + optionId + '\'' +
                ", actorTypeId='" + actorTypeId + '\'' +
                '}';
    }
}
