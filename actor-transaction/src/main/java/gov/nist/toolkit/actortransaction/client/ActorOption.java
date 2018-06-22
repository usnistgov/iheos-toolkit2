package gov.nist.toolkit.actortransaction.client;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.installation.shared.TestCollectionCode;

import java.io.Serializable;

public class ActorOption implements Serializable, IsSerializable {
    public IheItiProfile profileId;
    public String optionId;
    public String actorTypeId;



    public ActorOption() {

    }

    /**
     * Will parse encoded values such as:
     * actor        # Required option
     * actor_option
     * actor(profile) # Required
     * actor(profile)_option
     * @param tcCode
     */
    public ActorOption(TestCollectionCode tcCode)  {
        String tcNameStr = tcCode.getCode();
        String[] parts = tcNameStr.split("_");

        if (tcNameStr.contains("_")) {
            optionId = parts[1];
        } else {
               optionId = "";
         }
        if (tcNameStr.contains("(") && tcNameStr.contains(")"))  {
            String[] actorProfile = tcNameStr.split("\\(");
            actorTypeId = actorProfile[0];
            String profileId = actorProfile[1].replace(")","");
            this.profileId = IheItiProfile.find(profileId);
        } else {
            actorTypeId = parts[0];
            profileId = IheItiProfile.XDS;
        }
    }

    /**
     * Puts the object back into the encoded-value test collection name string.
     * @return
     */
    public TestCollectionCode getTestCollectionCode() {
        String optionCode = (optionId!=null && !"".equals(optionId))?"_"+optionId:"";
        String collectionName = "";
        if ((profileId==null || "".equals(profileId) || "xds".equals(profileId.toString()))) {
            if (optionId == null || "".equals(optionId))   {
                collectionName = actorTypeId;
            } else {
                collectionName =  actorTypeId + optionCode;
            }
        } else { // actor(profile)_option
            collectionName = actorTypeId + "(" + profileId + ")" + optionCode;
        }
        return new TestCollectionCode(collectionName);
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf
                .append("profile=")
                .append(profileId)
                .append(" option=")
                .append(optionId)
                .append(" actorType=")
                .append(actorTypeId);

        return buf.toString();
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

}
