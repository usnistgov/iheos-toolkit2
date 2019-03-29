package gov.nist.toolkit.xdstools2.client.inspector.contentFilter;

import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

public interface FilterFeature<T> extends IsWidget {
//    void setData(Map<S, Map<IndexFieldValue, List<T>>> data);
    void setData(List<T> data);
    void displayFilter();
    void hideFilter();
    boolean applyFilter();
    boolean isActive();
    boolean removeFilter();
}
