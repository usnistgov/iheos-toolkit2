package gov.nist.toolkit.xdstools2.client.inspector;

import gov.nist.toolkit.registrymetadata.client.MetadataObject;

import java.util.List;

public interface FilterFeature {
    void setData(List<? extends MetadataObject> metadataObjects);
    void displayFilter();
    void hideFilter();
    boolean applyFilter();
    boolean isActive();
    boolean removeFilter();
}
