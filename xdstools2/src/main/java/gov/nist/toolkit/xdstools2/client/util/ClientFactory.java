package gov.nist.toolkit.xdstools2.client.util;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import gov.nist.toolkit.xdstools2.client.inspector.mvp.InspectorActivity;
import gov.nist.toolkit.xdstools2.client.inspector.mvp.SimIndexInspector;
import gov.nist.toolkit.xdstools2.client.inspector.mvp.SimIndexInspectorActivity;
import gov.nist.toolkit.xdstools2.client.inspector.mvp.ResultInspector;
import gov.nist.toolkit.xdstools2.client.tabs.simMsgViewerTab.SimMsgViewer;
import gov.nist.toolkit.xdstools2.client.tabs.simMsgViewerTab.SimMsgViewerActivity;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.ConfActorActivity;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.SimLogActivity;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.TestInstanceActivity;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.ToolActivity;

/**
 * This is a factory that provides references to objects needed
 * throughout the application like the event bus.
 */
public interface ClientFactory {
    EventBus getEventBus();
    PlaceController getPlaceController();
    TestInstanceActivity getTestInstanceActivity();
    ToolActivity getToolActivity();
    ConfActorActivity getConfActorActivity();

    ToolkitServiceAsync getToolkitServices();

    SimLogActivity getSimLogActivity();

    SimMsgViewerActivity getSimMsgViewerActivity(SimMsgViewer place);

//    Activity getSubmitResourceActivity();
//    Activity getFhirSearchActivity();
//    SimMsgViewerView getSimMsgViewerView();

    InspectorActivity getInspectorActivity(ResultInspector place);
    SimIndexInspectorActivity getRegistryBrowserActivity(SimIndexInspector place);
}
