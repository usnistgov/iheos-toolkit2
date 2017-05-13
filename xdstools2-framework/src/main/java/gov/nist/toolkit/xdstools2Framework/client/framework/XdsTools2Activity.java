package gov.nist.toolkit.xdstools2Framework.client.framework;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import javax.inject.Inject;

/**
 *
 */
public class XdsTools2Activity extends AbstractActivity {

    @Inject
    ActivityDisplayer displayer;
    @Inject
    XdsTools2App xdsTools2App;

    @Override
    public void start(AcceptsOneWidget acceptsOneWidget, EventBus eventBus) {
        displayer.display(xdsTools2App.asWidget(),acceptsOneWidget,eventBus);
    }
}
