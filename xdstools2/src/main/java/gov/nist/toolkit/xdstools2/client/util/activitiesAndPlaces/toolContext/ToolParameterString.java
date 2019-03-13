package gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.toolContext;


/**
 * GWT Toolkit Place URL parameter string.
 */
public class ToolParameterString {
    /**
     * Input string
     */
    private String paramString;


    public ToolParameterString(String paramString) {
        this.paramString = paramString;
    }

    public String getParamString() {
        return paramString;
    }

    @Override
    public String toString() {
        return paramString;
    }
}
