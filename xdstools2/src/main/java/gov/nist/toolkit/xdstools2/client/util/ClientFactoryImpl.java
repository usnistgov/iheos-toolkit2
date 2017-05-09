package gov.nist.toolkit.xdstools2.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;
import gov.nist.toolkit.xdstools2.client.event.Xdstools2EventBus;
import gov.nist.toolkit.xdstools2.client.framework.ConfActorActivity;
import gov.nist.toolkit.xdstools2.client.framework.SimLogActivity;
import gov.nist.toolkit.xdstools2.client.framework.TestInstanceActivity;
import gov.nist.toolkit.xdstools2.client.framework.ToolActivity;

/**
 * This is the implementation of the factory that provides references to objects needed
 * throughout the application like the event bus.
 */
public class ClientFactoryImpl implements ClientFactory {
    private static final EventBus EVENT_BUS = new Xdstools2EventBus();
    private static final PlaceController PLACE_CONTROLLER = new PlaceController(EVENT_BUS);
    private static final TestInstanceActivity TEST_INSTANCE_ACTIVITY = new TestInstanceActivity();
    private static final ToolActivity TOOL_ACTIVITY = new ToolActivity();
    private static final ConfActorActivity CONF_ACTOR_ACTIVITY = new ConfActorActivity();
    private static final SimLogActivity SIM_LOG_ACTIVITY = new SimLogActivity();
    private static final ToolkitServiceAsync TOOLKIT_SERVICES = GWT.create(ToolkitService.class);

    @Override
    public EventBus getEventBus() {
        return EVENT_BUS;
    }

    @Override
    public PlaceController getPlaceController() {
        return PLACE_CONTROLLER;
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
        return TOOLKIT_SERVICES;
    }

    @Override
    public SimLogActivity getSimLogActivity() {
        return SIM_LOG_ACTIVITY;
    }
}
