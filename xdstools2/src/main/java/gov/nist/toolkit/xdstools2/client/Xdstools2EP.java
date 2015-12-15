package gov.nist.toolkit.xdstools2.client;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import gov.nist.toolkit.xdstools2.client.util.ClientFactory;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.TabPlace;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.Xdstools2ActivityMapper;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.Xdstools2PlaceHistoryMapper;

/**
 * Application entry point. It start the UI and initiate the Activity Place design.
 *
 */
public class Xdstools2EP implements EntryPoint{
    private static final ClientFactory CLIENT_FACTORY=GWT.create(ClientFactory.class);
    //    private static final EventBus EVENT_BUS= .getEventBus();
    private Xdstools2 xdstools2ActivityView=Xdstools2.getInstance();

    @Override
    public void onModuleLoad() {
        // start the application
        xdstools2ActivityView.run();

        PlaceController placeController = CLIENT_FACTORY.getPlaceController();


        // Start ActivityManager for the main widget with our ActivityMapper
        ActivityMapper activityMapper = new Xdstools2ActivityMapper(CLIENT_FACTORY);
        ActivityManager activityManager = new ActivityManager(activityMapper, CLIENT_FACTORY.getEventBus());
        // set the main widget container of the application (AcceptsOneWidget)
        activityManager.setDisplay(xdstools2ActivityView);

        // Start PlaceHistoryHandler with our PlaceHistoryMapper
        Xdstools2PlaceHistoryMapper historyMapper = GWT.create(Xdstools2PlaceHistoryMapper.class);
        PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);

        historyHandler.register(placeController, CLIENT_FACTORY.getEventBus(), new TabPlace("HOME"));

        RootLayoutPanel.get().add(xdstools2ActivityView);

//        RootPanel.get().add(xdstools2ActivityView);
        // Goes to place represented on URL or default place
        historyHandler.handleCurrentHistory();
    }
}
