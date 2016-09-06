package gov.nist.toolkit.xdstools2.client.event;

import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.SimpleEventBus;
import gov.nist.toolkit.xdstools2.client.event.EnvironmentChangedEvent.EnvironmentChangedEventHandler;

/**
 * Created by onh2 on 8/30/16.
 */
public class Xdstools2EventBus extends SimpleEventBus {
    /**
     * Method that handle actions that must be triggered when the environment changes.
     * @param handler
     * @return
     */
    public HandlerRegistration addEnvironmentChangedEventHandler(EnvironmentChangedEventHandler handler) {
        return addHandler(EnvironmentChangedEvent.TYPE, handler);
    }

    /**
     * Method that signals though the event bus to the application that the environment has changed.
     */
    public void fireEnvironmentChangedEvent(String selectedEnvironment) {
        fireEvent(new EnvironmentChangedEvent(selectedEnvironment));
    }
}
