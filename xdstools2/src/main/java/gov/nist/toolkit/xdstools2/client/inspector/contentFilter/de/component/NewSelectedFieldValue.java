package gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.component;

import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.DocumentEntryIndexField;

import java.util.List;

public class NewSelectedFieldValue {
    private IndexFieldFilterSelector<DocumentEntryIndexField, DocumentEntry> filterSelector;
    private List<String> values;

    public NewSelectedFieldValue(IndexFieldFilterSelector filterSelector, List<String> values) {
        this.filterSelector = filterSelector;
        this.values = values;
    }

    public DocumentEntryIndexField getField() {
        return filterSelector.getFieldType();
    }

    public IndexFieldFilterSelector getFilterSelector() {
        return filterSelector;
    }

    public List<String> getValues() {
        return values;
    }

}
