package gov.nist.toolkit.xdstools2.client.inspector;

import com.google.gwt.user.client.ui.IsWidget;
import gov.nist.toolkit.registrymetadata.client.MetadataObject;

import java.util.List;

public interface FilterFeature extends IsWidget {
    void setData(List<? extends MetadataObject> metadataObjects);
    void displayFilter();
    void hideFilter();
    boolean applyFilter();
    boolean isActive();
    boolean removeFilter();
}
