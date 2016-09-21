package gov.nist.toolkit.xdstools2.client.event;


import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by onh2 on 9/19/16.
 */
public class SimulatorUpdatedEvent extends GwtEvent<SimulatorUpdatedEvent.SimulatorUpdatedEventHandler> {

    public static final Type<SimulatorUpdatedEvent.SimulatorUpdatedEventHandler> TYPE = new Type<SimulatorUpdatedEvent.SimulatorUpdatedEventHandler>();

    @Override
    public Type<SimulatorUpdatedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SimulatorUpdatedEventHandler handler) {
        handler.onSimulatorsUpdate(this);
    }

    public interface SimulatorUpdatedEventHandler extends EventHandler{

        void onSimulatorsUpdate(SimulatorUpdatedEvent simulatorUpdatedEvent);
    }
}
