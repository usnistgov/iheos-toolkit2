package gov.nist.toolkit.desktop.client.home;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import gov.nist.toolkit.desktop.client.ActivityDisplayer;

import javax.inject.Inject;

/**
 *
 */
public class WelcomeActivity extends AbstractActivity {
    @Inject
    private ActivityDisplayer displayer;

    @Inject
    private WelcomePanel welcomePanel;

    @Override
    public void start(AcceptsOneWidget acceptsOneWidget, EventBus eventBus) {
        displayer.display(welcomePanel.asWidget(), "Welcome", this, acceptsOneWidget,eventBus);
    }
}
