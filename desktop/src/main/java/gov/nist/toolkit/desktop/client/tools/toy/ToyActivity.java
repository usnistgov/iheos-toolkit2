package gov.nist.toolkit.desktop.client.tools.toy;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.desktop.client.ActivityDisplayer;

import javax.inject.Inject;

/**
 *
 */
public class ToyActivity extends AbstractActivity {
    @Inject
    private ActivityDisplayer displayer;

    @Override
    public void start(AcceptsOneWidget acceptsOneWidget, EventBus eventBus) {
        GWT.log("Starting Toy Activity");
        displayer.display(getContainer(),acceptsOneWidget,eventBus);
    }

    private Widget getContainer() {
        SimpleLayoutPanel panel = new SimpleLayoutPanel();
        Label label = new Label("Toy");
        panel.add(label);
        return label;
    }
}
