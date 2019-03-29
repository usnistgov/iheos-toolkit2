package gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.component;

import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.DocumentEntryIndexField;

import java.util.List;

public class NewSelectedValue {
    private DocumentEntryIndexField field;
    private List<String> values;

    public NewSelectedValue(DocumentEntryIndexField field, List<String> values) {
        this.field = field;
        this.values = values;
    }

    public DocumentEntryIndexField getField() {
        return field;
    }

    public List<String> getValues() {
        return values;
    }

}
