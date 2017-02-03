package gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

/**
 * The URL will be created as http://APP#Tool:toolId
 * so the URL carries the name of this class!
 */
public class Tool extends Place {
    public static class Tokenizer implements PlaceTokenizer<Tool> {

        @Override
        public Tool getPlace(String s) {
            return new Tool(s);
        }

        @Override
        public String getToken(Tool tool) {
            return tool.getToolId().toString();
        }
    }

    private String toolId;

    public Tool(String toolId) { this.toolId = toolId; }

    public String getToolId() { return toolId; }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tool) {
            return toolId.equals(((Tool) obj).toolId);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return toolId.hashCode();
    }

    @Override
    public String toString() {
        return "Tool: "+this.getClass().getName() +
                ":'" + toolId +" loaded.";
    }

}
