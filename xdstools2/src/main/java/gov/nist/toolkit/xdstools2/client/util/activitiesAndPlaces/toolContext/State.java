package gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.toolContext;

import gov.nist.toolkit.xdsexception.client.TkNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class State {
    String paramString;
    final Map<Token, String> tokenValueMap = new HashMap<>();

    public State() {
    }

    public State(String paramString) {
        this.paramString = paramString;
    }

    public void restore() {
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
                        }
                    }
                }
            }
    }


    public String tokenize() {
        String tokenString = "";

        int length = Token.values().length;
        for (int cx=0; cx < length; cx++) {
            Token key = Token.values()[cx];
            String value = tokenValueMap.get(key);
            if (value!=null && !"".equals(value)) {
                tokenString += (key + "=" + value + (cx < length - 1 ? ";" : ""));
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
