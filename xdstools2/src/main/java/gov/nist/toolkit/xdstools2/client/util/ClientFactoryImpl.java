package gov.nist.toolkit.xdstools2.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;
import gov.nist.toolkit.xdstools2.client.ToolkitService;
import gov.nist.toolkit.xdstools2.client.ToolkitServiceAsync;
import gov.nist.toolkit.xdstools2.client.event.Xdstools2EventBus;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.ConfActorActivity;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.TestInstanceActivity;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.ToolActivity;

/**
 * This is the implementation of the factory that provides references to objects needed
 * throughout the application like the event bus.
 */
public class ClientFactoryImpl implements ClientFactory {
    private final static EventBus eventBus = new Xdstools2EventBus();
    private final static PlaceController placeController = new PlaceController(eventBus);
    private final static TestInstanceActivity TEST_INSTANCE_ACTIVITY = new TestInstanceActivity();
    private final static ToolActivity TOOL_ACTIVITY = new ToolActivity();
    private final static ConfActorActivity CONF_ACTOR_ACTIVITY = new ConfActorActivity();
    private final static ToolkitServiceAsync toolkitServices = GWT.create(ToolkitService.class);

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

    @Override
    public ConfActorActivity getConfActorActivity() {
        return CONF_ACTOR_ACTIVITY;
    }

    @Override
    public ToolkitServiceAsync getToolkitServices(){
        return toolkitServices;
    }
}
