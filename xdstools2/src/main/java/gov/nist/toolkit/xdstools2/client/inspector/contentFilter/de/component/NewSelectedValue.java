package gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.component;

import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.DocumentEntryIndexField;

import java.util.List;

public class NewSelectedValue {
    private IndexFieldFilterSelector component;
    private List<String> values;

    public NewSelectedValue(IndexFieldFilterSelector component, List<String> values) {
        this.component = component;
        this.values = values;
    }

    public DocumentEntryIndexField getField() {
        return component.getFieldType();
    }

    public IndexFieldFilterSelector getComponent() {
        return component;
    }

    public List<String> getValues() {
        return values;
    }

}
