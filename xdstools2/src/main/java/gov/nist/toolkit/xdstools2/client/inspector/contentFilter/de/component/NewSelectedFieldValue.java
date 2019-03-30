package gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.component;

import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.IndexFieldValue;
import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.DocumentEntryIndexField;

import java.util.Set;

public class NewSelectedFieldValue {
    private IndexFieldFilterSelector<DocumentEntryIndexField, DocumentEntry> filterSelector;
    private Set<IndexFieldValue> values;

    public NewSelectedFieldValue(IndexFieldFilterSelector filterSelector, Set<IndexFieldValue> values) {
        this.filterSelector = filterSelector;
        this.values = values;
    }

    public DocumentEntryIndexField getField() {
        return filterSelector.getFieldType();
    }

    public IndexFieldFilterSelector getFilterSelector() {
        return filterSelector;
    }

    public Set<IndexFieldValue> getValues() {
        return values;
    }
}
