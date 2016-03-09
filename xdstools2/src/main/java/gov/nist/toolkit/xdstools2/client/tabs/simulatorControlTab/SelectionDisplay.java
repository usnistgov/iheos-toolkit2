package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;

/**
 *
 */
public interface SelectionDisplay extends HasValue<List<String>> {
    void setData(List<String> data);

    List<Integer> getSelectedRows();

    void setSelectedRows(List<Integer> rows);

    Widget asWidget();
}
