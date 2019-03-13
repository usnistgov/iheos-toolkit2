package gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.toolContext;

import com.google.gwt.core.client.GWT;
import gov.nist.toolkit.xdsexception.client.TkNotFoundException;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;

public class ToolParameterParser {

    public static ToolParameterMap parse(ToolParameterString etp) {
        String paramString = etp.getParamString();
        // New delimiter formatted string
        if (paramString.indexOf("=")>-1) {
            return parseNewFormatString(paramString);
        }
        // Old delimiter formatted string
        else if (paramString.indexOf("/")>-1) {
            return parseOldFormatString(paramString);
        }
        // Unrecognized format
        else {
            throw new ToolkitRuntimeException("unable to parse: " + paramString);
        }
    }

    private static ToolParameterMap parseNewFormatString(String paramString) {
        ToolParameterMap dtp = new ToolParameterMap();
        String[] parts = paramString.split(";");

        if (parts!=null && parts.length>0) {
            for (int cx=0; cx<parts.length; cx++) {
                String part = parts[cx];
                String nameValue[] = part.split("=");
                if (nameValue!=null && nameValue.length==2) {
                    String name = nameValue[0];
                    String value = nameValue[1];
                    try {
                        ToolParameter pn = ToolParameter.findByPropertyName(name);
                        dtp.getParamMap().put(pn, value);
                    }  catch (TkNotFoundException tkNfe) {
                        // Ignore tokens not found.
                        GWT.log("Token not recognized: " + name);
                    }
                }
            }
        }
        return dtp;
    }

    private static ToolParameterMap parseOldFormatString(String paramString) {
        ToolParameterMap dtp = new ToolParameterMap();
        String[] parts = paramString.split("/");
        if (parts.length < 3) {
            dtp.getParamMap().put(ToolParameter.TEST_SESSION,"");
            dtp.getParamMap().put(ToolParameter.ACTOR,"");
        } else if (parts.length >= 3) {
            dtp.getParamMap().put(ToolParameter.ENVIRONMENT, parts[0].trim());
            dtp.getParamMap().put(ToolParameter.TEST_SESSION, parts[1].trim());
            dtp.getParamMap().put(ToolParameter.ACTOR, parts[2].trim());

            if (parts.length > 3)
                dtp.getParamMap().put(ToolParameter.PROFILE, parts[3].trim());
            if (parts.length > 4)
                dtp.getParamMap().put(ToolParameter.OPTION, parts[4].trim());
            if (parts.length > 5)
                dtp.getParamMap().put(ToolParameter.SYSTEM_ID, parts[5].trim());
        }
        return dtp;
    }

}
