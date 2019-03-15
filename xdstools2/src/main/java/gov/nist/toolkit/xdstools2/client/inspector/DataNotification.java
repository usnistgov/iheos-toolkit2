package gov.nist.toolkit.xdstools2.client.inspector;

import com.google.gwt.user.client.ui.TreeItem;
import gov.nist.toolkit.registrymetadata.client.MetadataCollection;
import gov.nist.toolkit.registrymetadata.client.MetadataObject;

public interface DataNotification {
    void onAddToHistory(MetadataCollection metadataCollection);
    void onObjectSelected(MetadataObjectWrapper objectWrapper);
    boolean inCompare();
    MetadataObject getComparable();
    void onCloseOffDetail(TreeItem currentTreeItem);
    void onViewModeChanged(MetadataInspectorTab.SelectedViewMode viewMode, MetadataObjectWrapper objectWrapper);
}
