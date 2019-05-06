package gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.toolContext;

public class ToolParameterFormatter {

    public static ToolParameterString format(ToolParameterMap tpm) {
        StringBuilder sb = new StringBuilder();

        int length = ToolParameter.values().length;
        for (int cx=0; cx < length; cx++) {
            ToolParameter key = ToolParameter.values()[cx];
            String value = tpm.getParamMap().get(key);
            if (value!=null && !"".equals(value)) {
                sb.append(key + "=" + value + ";");
            }
        }

        return new ToolParameterString(sb.toString());
    }
}
