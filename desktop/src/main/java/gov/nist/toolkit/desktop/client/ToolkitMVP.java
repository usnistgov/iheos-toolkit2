package gov.nist.toolkit.desktop.client;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import gov.nist.toolkit.desktop.client.root.ToolkitPlace;
import gov.nist.toolkit.desktop.client.utils.ToolkitActivityMapper;

/**
 * Entry point class
 */
public class ToolkitMVP implements EntryPoint {
  private Place defaultPlace = new ToolkitPlace("XDS Toolkit");
  private SimpleLayoutPanel appWidget = new SimpleLayoutPanel();
  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    ClientFactory clientFactory = GWT.create(ClientFactory.class);
    EventBus eventBus = clientFactory.getEventBus();
    PlaceController placeController = clientFactory.getPlaceController();

    // Start ActivityManager for the main widget with our ActivityMapper
    ActivityMapper activityMapper = new ToolkitActivityMapper(clientFactory);
    ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
    activityManager.setDisplay(appWidget);

    // Start PlaceHistoryHandler with our PlaceHistoryMapper
    ToolkitPlaceHistoryMapper historyMapper= GWT.create(ToolkitPlaceHistoryMapper.class);
    PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
    historyHandler.register(placeController, eventBus, defaultPlace);

    RootLayoutPanel.get().add(appWidget);

    // Goes to the place represented on URL else default place
    historyHandler.handleCurrentHistory();
  }
}
