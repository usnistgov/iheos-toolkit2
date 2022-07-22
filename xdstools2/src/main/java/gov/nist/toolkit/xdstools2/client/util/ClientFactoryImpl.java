package gov.nist.toolkit.xdstools2.client.util;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import gov.nist.toolkit.xdstools2.client.injector.Injector;
import gov.nist.toolkit.xdstools2.client.inspector.mvp.InspectorActivity;
import gov.nist.toolkit.xdstools2.client.inspector.mvp.SimIndexInspector;
import gov.nist.toolkit.xdstools2.client.inspector.mvp.SimIndexInspectorActivity;
import gov.nist.toolkit.xdstools2.client.inspector.mvp.ResultInspector;
import gov.nist.toolkit.xdstools2.client.resources.IconsResources;
import gov.nist.toolkit.xdstools2.client.tabs.simMsgViewerTab.SimMsgViewer;
import gov.nist.toolkit.xdstools2.client.tabs.simMsgViewerTab.SimMsgViewerActivity;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.ConfActorActivity;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.SimLogActivity;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.TestInstanceActivity;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.ToolActivity;

/**
 * This is the implementation of the factory that provides references to objects needed
 * throughout the application like the event bus.
 */
public class ClientFactoryImpl implements ClientFactory {
    private static final EventBus EVENT_BUS = Injector.INSTANCE.getEventBus(); //new Xdstools2EventBus();
    private static final PlaceController PLACE_CONTROLLER = new PlaceController(EVENT_BUS);
    private static final TestInstanceActivity TEST_INSTANCE_ACTIVITY = new TestInstanceActivity();
    private static final ToolActivity TOOL_ACTIVITY = new ToolActivity();
    private static final ConfActorActivity CONF_ACTOR_ACTIVITY = new ConfActorActivity();
    private static final SimLogActivity SIM_LOG_ACTIVITY = new SimLogActivity();
    private static final ToolkitServiceAsync TOOLKIT_SERVICES = GWT.create(ToolkitService.class);
//    private static final SimMsgViewerActivity MSG_VIEWER_ACTIVITY = GWT.create(SimMsgViewerActivity.class);
    private static final IconsResources IconsResources = GWT.create(IconsResources.class);

    public static IconsResources getIconsResources() {
        return IconsResources;
    }

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

    @Override
    public SimMsgViewerActivity getSimMsgViewerActivity(SimMsgViewer place) {
        return new SimMsgViewerActivity(place);
    }


    @Override
    public InspectorActivity getInspectorActivity(ResultInspector place) {
        return new InspectorActivity(place);
    }

    @Override
    public SimIndexInspectorActivity getRegistryBrowserActivity(SimIndexInspector place) {
       return new SimIndexInspectorActivity(place);
    }
}
