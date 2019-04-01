package gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.component;

import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.IndexFieldValue;
import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.DocumentEntryIndexField;

import java.util.Set;

public class NewSelectedFieldValue {
    private IndexFieldFilterSelector<DocumentEntryIndexField, DocumentEntry> filterSelector;
    private Set<IndexFieldValue> values;
    private boolean isInitialValue;
    private boolean clearSelection;

    public NewSelectedFieldValue(IndexFieldFilterSelector<DocumentEntryIndexField, DocumentEntry> filterSelector, Set<IndexFieldValue> values, boolean isInitialValue, boolean clearSelection) {
        this.filterSelector = filterSelector;
        this.values = values;
        this.isInitialValue = isInitialValue;
        this.clearSelection = clearSelection;
    }

    public IndexFieldFilterSelector<DocumentEntryIndexField, DocumentEntry> getFilterSelector() {
        return filterSelector;
    }

    public Set<IndexFieldValue> getValues() {
        return values;
    }

    public boolean isInitialValue() {
        return isInitialValue;
    }

    public boolean isClearSelection() {
        return clearSelection;
    }
}
