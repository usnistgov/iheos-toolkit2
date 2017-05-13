package gov.nist.toolkit.xdstools2.client.util;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.client.widgets.HorizontalFlowPanel;

/**
 * Build link to something in toolkit
 */
public class ToolkitLink implements IsWidget {
    private HorizontalFlowPanel panel = new HorizontalFlowPanel();

    /**
     *
     * @param relativeUrl - relative to toolkit base URL
     */
    public ToolkitLink(String title, String relativeUrl) {
        Anchor anchor = new Anchor();
        String baseUrl = XdsTools2Presenter.data().getToolkitBaseUrl();
        String url = baseUrl + relativeUrl;
        anchor.setText(url);
        anchor.setHref(url);
        anchor.setTarget("_blank");
        panel.add(new Label(title));
        panel.add(anchor);
    }

    @Override
    public Widget asWidget() {
        return panel;
    }
}
