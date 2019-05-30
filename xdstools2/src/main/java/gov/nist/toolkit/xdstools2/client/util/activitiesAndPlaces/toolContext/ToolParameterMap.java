package gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.toolContext;

import com.google.gwt.core.client.GWT;
import gov.nist.toolkit.xdsexception.client.TkNotFoundException;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Parameters of a GWT Toolkit Place in the form of an URL parameter string.
 */
public class ToolParameterMap {
    /**
     * Key-value map of the parsed parameters
     */
    private final Map<ToolParameter, String> paramMap = new HashMap<>();

    public ToolParameterMap() {
    }

    public Map<ToolParameter, String> getParamMap() {
        return paramMap;
    }

    public String getValue(ToolParameter t) {
        return paramMap.get(t);
    }
}
