package gov.nist.toolkit.xdstools2.client.tabs.simMsgViewerTab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractView;

import java.util.Map;

/**
 *
 */
public class SimMsgViewerView extends AbstractView<SimMsgViewerPresenter> {

    public SimMsgViewerView() {
        super();
        GWT.log("SimMsgViewerView create");
    }

    @Override
    protected Map<String, Widget> getPathToWidgetsMap() {
        return null;
    }

    @Override
    protected Widget buildUI() {
        SimpleLayoutPanel panel = new SimpleLayoutPanel();
        Label label = new Label("SimMsgViewer");
        panel.add(label);
        return label;
    }

    @Override
    protected void bindUI() {

    }
}
