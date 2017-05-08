package gov.nist.toolkit.desktop.client.modules.toolkit;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import gov.nist.toolkit.desktop.client.ClientFactory;

/**
 * This is the most basic Activity  - an empty toolkit display (no open tools)
 */
public class ToolkitActivity extends AbstractActivity implements ToolkitView.Presenter {

    @Inject
    private ClientFactory clientFactory;

    private String name;

    @Inject
    public ToolkitActivity(ToolkitPlace place, ClientFactory clientFactory) {
        this.name = place.getWelcomeName();
        this.clientFactory = clientFactory;
    }

    @Override
    public void start(AcceptsOneWidget acceptsOneWidget, EventBus eventBus) {
        ToolkitView toolkitView = clientFactory.getToolkitView();
        toolkitView.setName(name);
        toolkitView.setPresenter(this);
        acceptsOneWidget.setWidget(toolkitView.asWidget());
    }

    @Override
    public void goTo(Place place) {
        clientFactory.getPlaceController().goTo(place);
    }
}
