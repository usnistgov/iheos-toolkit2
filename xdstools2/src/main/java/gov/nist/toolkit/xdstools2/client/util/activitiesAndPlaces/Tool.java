package gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.toolContext.State;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.toolContext.Token;

/**
 * The URL will be created as
 * http://APP#Tool:toolId=name;ts=testSession;systemId=systemId;
 * so the URL carries the name of this class!
 *
 * See constants in ToolLauncher for toolId.
 */
public class Tool extends Place {
    private State state;
    private String toolId;

    public Tool(String paramString) {
       state = new State(paramString);
       state.restore();

       toolId = state.getValue(Token.TOOLID);
    }

    public String getToolId() { return toolId; }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tool) {
            Tool toCompare = (Tool)obj;
            return state.equals(toCompare.state);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return state.hashCode();
    }

    @Override
    public String toString() {
        return "Tool: "+this.getClass().getName() +
                ":'" + toolId +" loaded.";
    }
    public static class Tokenizer implements PlaceTokenizer<Tool> {

        @Override
        public Tool getPlace(String s) {
            return new Tool(s);
        }

        @Override
        public String getToken(Tool tool) {
            return tool.state.tokenize();
        }
    }

    public State getState() {
        return state;
    }
}
