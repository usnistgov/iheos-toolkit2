package gov.nist.toolkit.xdstools2.client.inspector.contentFilter;

import com.google.gwt.user.client.ui.IsWidget;
import gov.nist.toolkit.registrymetadata.client.MetadataObject;

import java.util.List;
import java.util.Map;

public interface FilterFeature extends IsWidget {
    void setData(Map<IndexField, Map<IndexFieldValue, List<? extends MetadataObject>>> data);
    void displayFilter();
    void hideFilter();
    boolean applyFilter();
    boolean isActive();
    boolean removeFilter();
}
