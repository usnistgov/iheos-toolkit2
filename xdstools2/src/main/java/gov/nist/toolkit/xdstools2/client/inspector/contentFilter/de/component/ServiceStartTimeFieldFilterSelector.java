package gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.component;

import gov.nist.toolkit.registrymetadata.client.DocumentEntry;
import gov.nist.toolkit.xdstools2.client.inspector.contentFilter.de.DocumentEntryIndexField;
import gov.nist.toolkit.xdstools2.client.util.SimpleCallbackT;

public class ServiceStartTimeFieldFilterSelector extends TimeFieldFilterSelector {

    public ServiceStartTimeFieldFilterSelector(String label, SimpleCallbackT<NewSelectedFieldValue> valueChangeNotification) {
        super(label, valueChangeNotification);
    }
    @Override
    public DocumentEntryIndexField getFieldType() {
        return DocumentEntryIndexField.SERVICE_START_TIME;
    }

    @Override
    public String getFieldValue(DocumentEntry de) {
        return de.serviceStartTime;
    }
}
