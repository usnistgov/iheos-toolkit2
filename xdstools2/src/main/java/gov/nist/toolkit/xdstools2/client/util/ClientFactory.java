package gov.nist.toolkit.xdstools2.client.util;

import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Created by onh2 on 12/9/2015.
 */
public interface ClientFactory {
    EventBus getEventBus();
    PlaceController getPlaceController();
}
