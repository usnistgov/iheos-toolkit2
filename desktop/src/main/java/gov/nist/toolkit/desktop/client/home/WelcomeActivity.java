package gov.nist.toolkit.desktop.client.home;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import gov.nist.toolkit.desktop.client.ActivityDisplayer;
import gov.nist.toolkit.desktop.client.abstracts.AbstractToolkitActivity;
import gov.nist.toolkit.desktop.client.abstracts.GenericMVP;

import javax.inject.Inject;

/**
 *
 */
public class WelcomeActivity extends AbstractToolkitActivity {
    @Inject
    private ActivityDisplayer displayer;

    @Inject
    private WelcomePanel welcomePanel;

    @Override
    public void start(AcceptsOneWidget acceptsOneWidget, EventBus eventBus) {
        displayer.display(welcomePanel.asWidget(), "Welcome", this, acceptsOneWidget,eventBus);
    }

    @Override
    public GenericMVP getMVP() {
        return null;
    }

    @Override
    public LayoutPanel onResume() {
        return null;
    }
}
