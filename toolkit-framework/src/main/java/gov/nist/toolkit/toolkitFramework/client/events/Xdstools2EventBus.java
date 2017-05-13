package gov.nist.toolkit.toolkitFramework.client.events;

import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 * Created by onh2 on 8/30/16.
 */
public class Xdstools2EventBus extends SimpleEventBus {
    /**
     * Method that handle actions that must be triggered when the environment changes.
     * @param handler
     * @return
     */
    public HandlerRegistration addEnvironmentChangedEventHandler(EnvironmentChangedEvent.EnvironmentChangedEventHandler handler) {
        return addHandler(EnvironmentChangedEvent.TYPE, handler);
    }

    /**
     * Method that signals though the event bus to the application that the environment has changed.
     * @param selectedEnvironment name of the newly selected environment.
     */
    public void fireEnvironmentChangedEvent(String selectedEnvironment) {
        fireEvent(new EnvironmentChangedEvent(selectedEnvironment));
    }

    /**
     * Notify the other tabs though the eventbus that the simulators have been updated.
     */
    public void fireSimulatorsUpdatedEvent() {
        fireEvent(new SimulatorUpdatedEvent());
    }

    /**
     * Enable to know when the event bus is notified that the simulators have been updated and the handler itself
     * tells what to do after getting notified.
     * @param handler
     * @return
     */
    public HandlerRegistration addSimulatorsUpdatedEventHandler(SimulatorUpdatedEvent.SimulatorUpdatedEventHandler handler){
        return addHandler(SimulatorUpdatedEvent.TYPE,handler);
    }

    /**
     * Notify the event bus that the actors config has changed.
     */
    public void fireActorsConfigUpdatedEvent() {
        fireEvent(new ActorConfigUpdatedEvent());
    }

    /**
     * Enable to know when the event bus is notified that the actors config has changed and the handler itself
     * tells what to do after getting notified.
     * @param handler
     * @return
     */
    public HandlerRegistration addActorsConfigUpdatedEventHandler(ActorConfigUpdatedEvent.ActorConfigUpdatedEventHandler handler){
        return addHandler(ActorConfigUpdatedEvent.TYPE,handler);
    }

    /**
     * notify the other tabs through the event bus that there has been a change in the list of favorite PIDs.
     */
    public void fireFavoritePidsUpdateEvent() {
        fireEvent(new FavoritePidsUpdatedEvent());
    }

    /**
     * Add a new handler that will catch the notification about the list of favorite PIDs modification.
     * @param handler
     * @return
     */
    public HandlerRegistration addFavoritePidsUpdateEventHandler(FavoritePidsUpdatedEvent.FavoritePidsUpdatedEventHandler handler){
        return addHandler(FavoritePidsUpdatedEvent.TYPE,handler);
    }

    /**
     * Notify the eventbus that a opened tab has been selected.
     *
     */
    public void fireTabSelectedEvent(String tabName){fireEvent(new TabSelectedEvent(tabName));}

    /**
     * Add a new handler that will catch the notification about the list of favorite PIDs modification.
     * @param handler
     * @return
     */
    public HandlerRegistration addTabSelectedEventHandler(TabSelectedEvent.TabSelectedEventHandler handler){
        return addHandler(TabSelectedEvent.TYPE,handler);
    }
}
