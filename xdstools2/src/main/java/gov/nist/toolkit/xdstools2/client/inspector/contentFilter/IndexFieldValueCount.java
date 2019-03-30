package gov.nist.toolkit.xdstools2.client.inspector.contentFilter;

import java.util.Set;

public interface IndexFieldValueCount {
    void doUpdateCount(IndexFieldValue fieldValue, int count);
    Set<IndexFieldValue> getSelectedValues();
    void mapFieldValuesToCounterLabel();
}
