package gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

/**
 * The URL will be created as
 * http://APP#ConfActor:TestSessionName/ActorType
 */
public class ConfActor extends Place {
    public static class Tokenizer implements PlaceTokenizer<ConfActor> {

        @Override
        public ConfActor getPlace(String s) {
            return new ConfActor(s);
        }

        @Override
        public String getToken(ConfActor ca) {
            return ca.environmentName + "/" + ca.testSessionName + "/" + ca.actorType;
        }
    }

    private String environmentName;
    private String testSessionName;
    private String actorType;

    public ConfActor(String configuration) {
        String[] parts = configuration.split("/");
        if (parts.length < 3) {
            testSessionName = "";
            actorType = "";
        } else {
            environmentName = parts[0].trim();
            testSessionName = parts[1].trim();
            actorType = parts[2].trim();
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
        return "ConfActor: " + environmentName + "/" + testSessionName + "/" + actorType;
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
}
