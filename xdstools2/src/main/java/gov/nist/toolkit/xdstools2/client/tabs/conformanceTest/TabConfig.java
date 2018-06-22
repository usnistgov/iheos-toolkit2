package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by skb1 on 7/18/2017.
 */
public class TabConfig implements Serializable, IsSerializable {
    private String label;
    /**
     * Tab type: samples: actor, profile, option.
     */
    private String type;
    private String tcCode;
    private Boolean externalStart;
    private String displayColorCode;
    private boolean isVisible;

    private List<TabConfig> childTabConfigs = new ArrayList<TabConfig>();

    public enum TAB_TYPE {Actor, Profile, Option};

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf
                .append("isVisible=")
                .append(isVisible)
                .append("label=")
                .append(label)
                .append(" type=")
                .append(type)
                .append(" ")
                .append(" tcCode=")
                .append(tcCode)
                .append(" externalStart=")
                .append(externalStart);

        return buf.toString();
    }

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

    public boolean isExternalStart() {
        return externalStart != null && externalStart;
    }

    public void setExternalStart(Boolean externalStart) {
        this.externalStart = externalStart;
    }

    public List<TabConfig> getChildTabConfigs() {
        return childTabConfigs;
    }

    public boolean hasChildren() {
        return childTabConfigs.size()>0;
    }

    public TabConfig getFirstChildTabConfig() {
        if (childTabConfigs.size()==0)
            return null;
        return childTabConfigs.get(0);
    }


    public void setChildTabConfigs(List<TabConfig> childTabConfigs) {
        this.childTabConfigs = childTabConfigs;
    }

    /**
     * Represents a tab group header or the document root where the type attribute is null or does not exist in both cases.
     * @return
     */
    public boolean isHeader() {
       return type==null;
    }

    public String getDisplayColorCode() {
        return displayColorCode;
    }

    public void setDisplayColorCode(String displayColorCode) {
        this.displayColorCode = displayColorCode;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public static TabConfig clone(final TabConfig src) {
       TabConfig dest = new TabConfig();

       dest.label = src.label;
       dest.type = src.type;
       dest.tcCode = src.tcCode;
       dest.externalStart = src.externalStart;
       dest.displayColorCode = src.displayColorCode;
       dest.isVisible = src.isVisible;

       if (src.hasChildren()) {
            List<TabConfig> children = src.getChildTabConfigs();
            for (TabConfig tc : children) {
                dest.childTabConfigs.add(clone(tc));
            }
       }

       return dest;
    }

}
