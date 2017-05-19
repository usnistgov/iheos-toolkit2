package gov.nist.toolkit.xdstools2Framework.client.framework;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.*;

/**
 *
 */
public class Activity2 extends AbstractActivity {
    Label label1 = new Label("label2");
    Button button1 = new Button("Button2");
    FlowPanel panel1 = new FlowPanel();

    public Activity2() {
        panel1.add(label1);
        panel1.add(button1);
    }

    @Override
    public void start(AcceptsOneWidget acceptsOneWidget, EventBus eventBus) {
        acceptsOneWidget.setWidget(panel1);

    }

    public Widget getWidget() { return panel1; }
}
