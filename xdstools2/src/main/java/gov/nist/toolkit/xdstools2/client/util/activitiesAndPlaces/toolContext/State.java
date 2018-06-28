package gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.toolContext;

import com.google.gwt.core.client.GWT;
import gov.nist.toolkit.xdsexception.client.TkNotFoundException;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * State of a GWT Toolkit Place in the form of an URL parameter string.
 */
public class State {
    /**
     * Input string
     */
    String paramString;
    /**
     * Output key-value map of the parsed input
     */
    final Map<Token, String> tokenValueMap = new HashMap<>();

    public State() {
    }

    public State(String paramString) {
        this.paramString = paramString;
    }

    public void restore() {
        // New delimiter
        if (paramString.indexOf(";")>-1) {
           parseNewFormatString();
        }
        // Old delimiter
        else if (paramString.indexOf("/")>-1) {
           parseOldFormatString();
        }
        // Unrecognized format
        else {
            throw new ToolkitRuntimeException("unable to parse: " + paramString);
        }
    }

    private void parseNewFormatString() {
        String[] parts = paramString.split(";");

        if (parts!=null && parts.length>0) {
            for (int cx=0; cx<parts.length; cx++) {
                String part = parts[cx];
                String nameValue[] = part.split("=");
                if (nameValue!=null && nameValue.length==2) {
                    String name = nameValue[0];
                    String value = nameValue[1];
                    try {
                        Token pn = Token.findByPropertyName(name);
                        tokenValueMap.put(pn, value);
                    }  catch (TkNotFoundException tkNfe) {
                        // Ignore tokens not found.
                        GWT.log("Token not recognized: " + name);
                    }
                }
            }
        }
    }

    private void parseOldFormatString() {
        String[] parts = paramString.split("/");
        if (parts.length < 3) {
            tokenValueMap.put(Token.TEST_SESSION,"");
            tokenValueMap.put(Token.ACTOR,"");
        } else if (parts.length >= 3) {
            tokenValueMap.put(Token.ENVIRONMENT, parts[0].trim());
            tokenValueMap.put(Token.TEST_SESSION, parts[1].trim());
            tokenValueMap.put(Token.ACTOR, parts[2].trim());

            if (parts.length > 3)
                tokenValueMap.put(Token.PROFILE, parts[3].trim());
            if (parts.length > 4)
                tokenValueMap.put(Token.OPTION, parts[4].trim());
            if (parts.length > 5)
                tokenValueMap.put(Token.SYSTEM_ID, parts[5].trim());
        }
    }


    public String tokenize() {
        String tokenString = "";

        int length = Token.values().length;
        for (int cx=0; cx < length; cx++) {
            Token key = Token.values()[cx];
            String value = tokenValueMap.get(key);
            if (value!=null && !"".equals(value)) {
                tokenString += (key + "=" + value + ";");
            }
        }

        return tokenString;
    }

    @Override
    public String toString() {
        return tokenize();
    }

    public String getValue(Token t) {
        return tokenValueMap.get(t);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof State)) return false;
        State that = (State) o;
        return Objects.equals(tokenValueMap, that.tokenValueMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tokenValueMap);
    }

    public String getParamString() {
        return paramString;
    }

    public void setParamString(String paramString) {
        this.paramString = paramString;
    }
}
