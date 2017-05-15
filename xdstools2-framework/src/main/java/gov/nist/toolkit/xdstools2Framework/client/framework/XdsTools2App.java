package gov.nist.toolkit.xdstools2Framework.client.framework;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import javax.inject.Inject;

/**
 *
 */
public class XdsTools2App implements IsWidget {
    private static final TkGinInjector INJECTOR = TkGinInjector.INSTANCE;
    private final EventBus eventBus = INJECTOR.getEventBus();

    private SimplePanel activityPanel = new SimplePanel();

    @Inject
    private XdsTools2AppView appView;
    private XdsTools2Presenter appPresenter;

    public XdsTools2App() {
        appView = INJECTOR.getXdsTools2AppView();

        assert(appView != null);
        assert(eventBus != null);

        appPresenter = INJECTOR.getXdsTools2AppPresenter();
        appPresenter.setView(appView);

        PlaceController placeController = INJECTOR.getPlaceController();

        XdsTools2ActivityMapper activityMapper = new XdsTools2ActivityMapper();
        ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
        activityManager.setDisplay(activityPanel);

        XdsTools2AppPlaceHistoryMapper historyMapper = GWT.create(XdsTools2AppPlaceHistoryMapper.class);
        PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);

        historyHandler.register(placeController, eventBus, new WelcomePlace("Welcome"));

        activityPanel.add(appView.asWidget());

        historyHandler.handleCurrentHistory();
    }

    @Override
    public Widget asWidget() {
        return activityPanel;
    }

    public XdsTools2AppView getAppView(){
        return appView;
    }

    public static TkGinInjector getInjector() {
        return INJECTOR;
    }

    public EventBus getEventBus() {
        return eventBus;
    }


}
