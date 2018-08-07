package gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.toolContext.State;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.toolContext.Token;

/**
 * The URL will be created as
 * http://APP#ConfActor:env=EnvironmentName;ts=TestSessionName;actor=ActorType;profile=profile;opt=option;system=system
 */
public class ConfActor extends Place {
    private State state;

    public ConfActor(String paramString) {
       state = new State(paramString);
       state.restore();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ConfActor) {
            ConfActor toCompare = (ConfActor) obj;
            return state.equals(toCompare.state);
        }
        return false;
    }

    @Override
    public String toString() {
        return "ConfActor: " + state.tokenize();
    }

    public String getTestSessionName() {
        return state.getValue(Token.TEST_SESSION);
    }

    public String getActorType() {
        return state.getValue(Token.ACTOR);
    }

    public String getEnvironmentName() {
        return state.getValue(Token.ENVIRONMENT);
    }

    public String getProfileId() {
        return state.getValue(Token.PROFILE);
    }

    public String getOptionId() {
        return state.getValue(Token.OPTION);
    }

    public String getSystemName() {
        return state.getValue(Token.SYSTEM_ID);
    }

    public static class Tokenizer implements PlaceTokenizer<ConfActor> {
        @Override
        public ConfActor getPlace(String s) {
            return new ConfActor(s);
        }

        @Override
        public String getToken(ConfActor ca) {
            return ca.state.tokenize();
        }
    }

    public State getState() {
        return state;
    }
}
