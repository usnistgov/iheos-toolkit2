package gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.component;

import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.DocumentEntryIndexField;
import gov.nist.toolkit.xdstools2.client.util.SimpleCallbackT;

public class CreationTimeFieldFilterSelector extends TimeFieldFilterSelector {

    public CreationTimeFieldFilterSelector(String label, SimpleCallbackT<NewSelectedFieldValue> valueChangeNotification) {
        super(label, valueChangeNotification);
    }
    @Override
    public DocumentEntryIndexField getFieldType() {
        return DocumentEntryIndexField.CREATION_TIME;
    }
}
