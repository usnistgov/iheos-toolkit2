package gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.component;

import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.DocumentEntryIndexField;
import gov.nist.toolkit.xdstools2.client.util.SimpleCallbackT;

public class ServiceStopTimeFieldFilterSelector extends TimeFieldFilterSelector {

    public ServiceStopTimeFieldFilterSelector(String label, SimpleCallbackT<NewSelectedFieldValue> valueChangeNotification) {
        super(label, valueChangeNotification);
    }
    @Override
    public DocumentEntryIndexField getFieldType() {
        return DocumentEntryIndexField.SERVICE_STOP_TIME;
    }
}
