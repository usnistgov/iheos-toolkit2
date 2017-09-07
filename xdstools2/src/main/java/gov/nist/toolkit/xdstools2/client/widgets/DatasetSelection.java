package gov.nist.toolkit.xdstools2.client.widgets;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.datasets.shared.DatasetModel;

import java.util.List;

/**
 *
 */
public class DatasetSelection implements IsWidget {
    private final List<DatasetModel> content;
    private final VerticalPanel topPanel = new VerticalPanel();

    public DatasetSelection(List<DatasetModel> content) {
        this.content = content;
    }

    @Override
    public Widget asWidget() {
        return null;
    }
}
