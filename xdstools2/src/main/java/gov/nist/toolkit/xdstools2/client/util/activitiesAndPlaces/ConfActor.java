package gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

/**
 * The URL will be created as
 * http://APP#ConfActor:TestSessionName/ActorType/profile/option
 */
public class ConfActor extends Place {
    public static class Tokenizer implements PlaceTokenizer<ConfActor> {

        @Override
        public ConfActor getPlace(String s) {
            return new ConfActor(s);
        }

        @Override
        public String getToken(ConfActor ca) {
            return ca.environmentName + "/" + ca.testSessionName + "/" + ca.actorType
                    + ((ca.profileId!=null)?("/"+ca.profileId + ((ca.optionId!=null)?"/"+ca.optionId:"")):"");
        }
    }

    private String environmentName;
    private String testSessionName;
    private String actorType;
    private String profileId;
    private String optionId;

    public ConfActor(String configuration) {
        String[] parts = configuration.split("/");
        if (parts.length < 3) {
            testSessionName = "";
            actorType = "";
        } else if (parts.length >= 3) {
            environmentName = parts[0].trim();
            testSessionName = parts[1].trim();
            actorType = parts[2].trim();

            if (parts.length > 3)
                profileId = parts[3].trim();
            if (parts.length > 4)
                optionId = parts[4].trim();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ConfActor) {
            ConfActor ca = (ConfActor) obj;
            return ca.environmentName.equals(environmentName) && ca.testSessionName.equals(testSessionName) && ca.actorType.equals(actorType);
        }
        return false;
    }

    @Override
    public String toString() {
        return "ConfActor: " + environmentName + "/" + testSessionName + "/" + actorType
                + ((profileId!=null)?("/"+profileId + ((optionId!=null)?"/"+optionId:"")):"");
    }

    public String getTestSessionName() {
        return testSessionName;
    }

    public String getActorType() {
        return actorType;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public String getProfileId() {
        return profileId;
    }

    public String getOptionId() {
        return optionId;
    }
}
