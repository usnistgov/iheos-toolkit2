package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by skb1 on 7/18/2017.
 */
public class TabConfig {
    private String label;
    private String type;
    private String tcCode;
    private Boolean externalStart;

    private List<TabConfig> childTabConfigs = new ArrayList<TabConfig>();

    public TabConfig() {}

    public TabConfig(String label) {
        this.label = label;
    }

    public TabConfig(String label, String type, String tcCode) {
        this.label = label;
        this.type = type;
        this.tcCode = tcCode;
    }

    public TabConfig(String label, String type, String tcCode, Boolean externalStart) {
        this.label = label;
        this.type = type;
        this.tcCode = tcCode;
        this.externalStart = externalStart;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTcCode() {
        return tcCode;
    }

    public void setTcCode(String tcCode) {
        this.tcCode = tcCode;
    }

    public Boolean getExternalStart() {
        return externalStart;
    }

    public void setExternalStart(Boolean externalStart) {
        this.externalStart = externalStart;
    }

    public List<TabConfig> getChildTabConfigs() {
        return childTabConfigs;
    }

    public void setChildTabConfigs(List<TabConfig> childTabConfigs) {
        this.childTabConfigs = childTabConfigs;
    }
}
