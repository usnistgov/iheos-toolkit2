package gov.nist.toolkit.xdstools2Framework.client.framework;

import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;
import gov.nist.toolkit.xdstools2.client.util.ToolkitServiceAsync;

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
}
