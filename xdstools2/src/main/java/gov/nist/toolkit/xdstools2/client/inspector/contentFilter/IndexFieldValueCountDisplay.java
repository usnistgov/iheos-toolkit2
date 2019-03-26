package gov.nist.toolkit.xdstools2.client.inspector.contentFilter;

import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.IndexFieldValue;
import gov.nist.toolkit.xdstools2.client.util.SimpleCallback;

import java.util.List;
import java.util.Set;

public interface IndexFieldValueCountDisplay {
    void doUpdateCount(IndexFieldValue fieldValue, int count);
    Set<IndexFieldValue> getFieldValues();
    void mapFieldValueToCountLabel();
    void doValueChangeNotification(SimpleCallback callback);
}
