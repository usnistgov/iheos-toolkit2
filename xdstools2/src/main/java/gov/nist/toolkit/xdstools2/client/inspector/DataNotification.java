package gov.nist.toolkit.xdstools2.client.inspector;

import gov.nist.toolkit.registrymetadata.client.MetadataCollection;

public interface DataNotification {
    void onAddToHistory(MetadataCollection metadataCollection);
    void onObjectSelected(MetadataObjectWrapper objectWrapper);
}
