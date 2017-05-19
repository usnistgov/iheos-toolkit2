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
    static private int counter = 0;
    private int myIndex;
    private String name = "";

    @Inject
    private ActivityDisplayer displayer;

    @Override
    public void start(AcceptsOneWidget acceptsOneWidget, EventBus eventBus) {
        GWT.log("Starting Toy Activity");
        displayer.display(getContainer(), this, acceptsOneWidget,eventBus);
    }

    private Widget getContainer() {
        SimpleLayoutPanel panel = new SimpleLayoutPanel();
        myIndex = counter++;
        Label label = new Label("Toy " + myIndex + " " + name);
        panel.add(label);
        return label;
    }

    @Override
    public void onStop() {
        GWT.log("Stopping Toy Activity " + myIndex);
    }

    public void setName(String name) {
        this.name = name;
    }
}
