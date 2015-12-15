package gov.nist.toolkit.actorfactory.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class SelectionManager implements Serializable {
    boolean multipleValue;
    String label;
    List<String> values;
    SelectionManagerSelector selector;

    public SelectionManager() {}

    public SelectionManager(String label, boolean multipleValue, SelectionManagerSelector selector) {
        this.label = label;
        this.multipleValue = multipleValue;
        this.selector = selector;
        values = new ArrayList<>();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) { this.values = values; }

    // load values into values
    public void loadValues() {
        this.values = selector.select(this);
    }

    public boolean isMultipleValue() {
        return multipleValue;
    }

    public void setMultipleValue(boolean multipleValue) {
        this.multipleValue = multipleValue;
    }
}
