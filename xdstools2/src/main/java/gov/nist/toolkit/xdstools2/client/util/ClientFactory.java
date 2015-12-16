package gov.nist.toolkit.xdstools2.client.util;

import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;
import gov.nist.toolkit.xdstools2.client.Xdstools2Activity;

/**
 * This is a factory that provides references to objects needed
 * throughout the application like the event bus.
 */
public interface ClientFactory {
    EventBus getEventBus();
    PlaceController getPlaceController();
    Xdstools2Activity getXdstools2Activity();
}
