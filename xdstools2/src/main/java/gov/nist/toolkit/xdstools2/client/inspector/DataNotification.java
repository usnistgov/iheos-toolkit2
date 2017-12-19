package gov.nist.toolkit.xdstools2.client.inspector;

import com.google.gwt.user.client.ui.TreeItem;
import gov.nist.toolkit.registrymetadata.client.MetadataCollection;

public interface DataNotification {
    void onAddToHistory(MetadataCollection metadataCollection);
    void onObjectSelected(MetadataObjectWrapper objectWrapper);
    boolean inCompare();
    void onCloseOffDetail(TreeItem currentTreeItem);
    void onHistoryContentModeChanged(MetadataObjectWrapper objectWrapper);
}
