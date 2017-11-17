package gov.nist.toolkit.xdstools2.client.inspector.mvp.widgets;

import java.util.List;

public class NamedBoxProperties {
    private String name;
    private List<String> description;
    private java.util.List<String> tooltips;

    public NamedBoxProperties(String name, List<String> description, List<String> tooltips) {
        this.name = name;
        this.description = description;
        this.tooltips = tooltips;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public List<String> getTooltips() {
        return tooltips;
    }

    public void setTooltips(List<String> tooltips) {
        this.tooltips = tooltips;
    }
}
