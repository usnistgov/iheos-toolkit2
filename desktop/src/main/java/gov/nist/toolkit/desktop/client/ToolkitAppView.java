package gov.nist.toolkit.desktop.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is the toolkit wrapper (main toolkit window) - the place were tools are displayed
 */
public class ToolkitAppView extends FlowPanel {

    public ToolkitAppView() {
        GWT.log("ToolkitAppView");
        add(new Label("This is toolkit"));
    }

    public void setWidget(Widget w) {
        add(w);
    }

    public IsWidget getWidget() {
        return this;
    }
}
