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
 * http://APP#Tool:toolId=name;ts=testSession;systemId=systemId;
 * so the URL carries the name of this class!
 *
 * See constants in ToolLauncher for toolId.
 */
public class Tool extends Place {
    private ToolParameterMap tpm;
    private String toolId;

    public Tool(String paramString) {
       tpm = ToolParameterParser.parse(new ToolParameterString(paramString));
       toolId = tpm.getValue(ToolParameter.TOOLID);
    }

    public String getToolId() { return toolId; }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tool) {
            Tool toCompare = (Tool)obj;
            return tpm.equals(toCompare.tpm);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return ToolParameterFormatter.format(tpm).toString().hashCode();
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
            return ToolParameterFormatter.format(tool.tpm).toString();
        }
    }

    public ToolParameterMap getTpm() {
        return tpm;
    }
}
