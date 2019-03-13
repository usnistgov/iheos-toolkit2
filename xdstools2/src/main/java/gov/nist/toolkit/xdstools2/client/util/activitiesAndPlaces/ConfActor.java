package gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.toolContext.ToolParameter;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.toolContext.ToolParameterFormatter;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.toolContext.ToolParameterMap;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.toolContext.ToolParameterParser;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.toolContext.ToolParameterString;

/**
 * The URL will be created as
 * http://APP#ConfActor:env=EnvironmentName;ts=TestSessionName;actor=ActorType;profile=profile;opt=option;system=system
 */
public class ConfActor extends Place {
    private ToolParameterMap tpm;

    public ConfActor(String paramString) {
       tpm = ToolParameterParser.parse(new ToolParameterString(paramString));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ConfActor) {
            ConfActor toCompare = (ConfActor) obj;
            return tpm.equals(toCompare.getTpm());
        }
        return false;
    }

    @Override
    public String toString() {
        return "ConfActor: " + ToolParameterFormatter.format(tpm).toString();
    }

    public String getTestSessionName() {
        return tpm.getValue(ToolParameter.TEST_SESSION);
    }

    public String getActorType() {
        return tpm.getValue(ToolParameter.ACTOR);
    }

    public String getEnvironmentName() {
        return tpm.getValue(ToolParameter.ENVIRONMENT);
    }

    public String getProfileId() {
        return tpm.getValue(ToolParameter.PROFILE);
    }

    public String getOptionId() {
        return tpm.getValue(ToolParameter.OPTION);
    }

    public String getSystemName() {
        return tpm.getValue(ToolParameter.SYSTEM_ID);
    }

    public static class Tokenizer implements PlaceTokenizer<ConfActor> {
        @Override
        public ConfActor getPlace(String s) {
            return new ConfActor(s);
        }

        @Override
        public String getToken(ConfActor ca) {
            return ToolParameterFormatter.format(ca.getTpm()).toString();
        }
    }

    public ToolParameterMap getTpm() {
        return tpm;
    }
}
