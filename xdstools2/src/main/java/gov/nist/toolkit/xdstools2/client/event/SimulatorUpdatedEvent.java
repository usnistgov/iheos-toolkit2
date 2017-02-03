package gov.nist.toolkit.xdstools2.client.event;


import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Event thrown when the simulators change (creation, deletion, modification).
 * @see SimulatorUpdatedEventHandler
 * Created by onh2 on 9/19/16.
 */
public class SimulatorUpdatedEvent extends GwtEvent<SimulatorUpdatedEvent.SimulatorUpdatedEventHandler> {

    public static final Type<SimulatorUpdatedEvent.SimulatorUpdatedEventHandler> TYPE = new Type<>();

    @Override
    public Type<SimulatorUpdatedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SimulatorUpdatedEventHandler handler) {
        handler.onSimulatorsUpdate(this);
    }

    /**
     * Event handler interface for a simulator change (environment selection changed).
     * @see SimulatorUpdatedEvent
     */
    public interface SimulatorUpdatedEventHandler extends EventHandler{
        /**
         * Actions to be executed on the class that catches thd {@link SimulatorUpdatedEvent event} when there is
         * a simulator update (creation, deletion, modification).
         */
        void onSimulatorsUpdate(SimulatorUpdatedEvent simulatorUpdatedEvent);
    }
}
