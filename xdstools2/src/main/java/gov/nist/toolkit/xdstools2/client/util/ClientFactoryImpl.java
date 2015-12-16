package gov.nist.toolkit.xdstools2.client.util;

import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import gov.nist.toolkit.xdstools2.client.Xdstools2Activity;

/**
 * This is the implementation of the factory that provides references to objects needed
 * throughout the application like the event bus.
 */
public class ClientFactoryImpl implements ClientFactory {
    private final static EventBus eventBus = new SimpleEventBus();
    private final static PlaceController placeController = new PlaceController(eventBus);
    private final static Xdstools2Activity xdstools2Activity = new Xdstools2Activity();

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public PlaceController getPlaceController() {
        return placeController;
    }

    @Override
    public Xdstools2Activity getXdstools2Activity(){
        return xdstools2Activity;
    }
}
