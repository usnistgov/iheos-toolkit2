package gov.nist.toolkit.xdstools2.client.util;

import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.TestInstanceActivity;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.ToolActivity;

/**
 * This is the implementation of the factory that provides references to objects needed
 * throughout the application like the event bus.
 */
public class ClientFactoryImpl implements ClientFactory {
    private final static EventBus eventBus = new SimpleEventBus();
    private final static PlaceController placeController = new PlaceController(eventBus);
    private final static TestInstanceActivity TEST_INSTANCE_ACTIVITY = new TestInstanceActivity();
    private final static ToolActivity TOOL_ACTIVITY = new ToolActivity();

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public PlaceController getPlaceController() {
        return placeController;
    }

    @Override
    public TestInstanceActivity getTestInstanceActivity(){
        return TEST_INSTANCE_ACTIVITY;
    }

    @Override
    public ToolActivity getToolActivity() {
        return TOOL_ACTIVITY;
    }
}
