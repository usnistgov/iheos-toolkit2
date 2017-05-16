package gov.nist.toolkit.xdstools2Framework.client.framework;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.toolkitFramework.client.injector.ToolkitEventBus;

/**
 *
 */
public class XdsTools2App implements IsWidget {
    private static final TkGinInjector INJECTOR = TkGinInjector.INSTANCE;
    private final ToolkitEventBus eventBus = INJECTOR.getEventBus();

    private SimplePanel activityPanel = new SimplePanel();

    private XdsTools2AppView appView;

    public XdsTools2App() {
        GWT.log("In XdsTools2App");

        appView = INJECTOR.getXdsTools2AppView();

        GWT.log("setting placecontroller");

        PlaceController placeController = INJECTOR.getPlaceController();

        GWT.log("placecontroller set");


        GWT.log("setView done");
        XdsTools2ActivityMapper activityMapper = new XdsTools2ActivityMapper();
        ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
        activityManager.setDisplay(activityPanel);

        GWT.log("before mapper");

        XdsTools2AppPlaceHistoryMapper historyMapper = GWT.create(XdsTools2AppPlaceHistoryMapper.class);
        PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);

        historyHandler.register(placeController, eventBus, new WelcomePlace("Welcome"));

        GWT.log("adding view to activity panel");

        activityPanel.add(appView.asWidget());

        GWT.log("handle history");

//        historyHandler.handleCurrentHistory();
        GWT.log("end of app");
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


}
