package gov.nist.toolkit.xdstools2.client.event;


import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by onh2 on 9/20/16.
 */
public class ActorConfigUpdatedEvent extends GwtEvent<ActorConfigUpdatedEvent.ActorConfigUpdatedEventHandler> {
    public static final Type<ActorConfigUpdatedEventHandler> TYPE = new Type<ActorConfigUpdatedEventHandler>();

    @Override
    public Type<ActorConfigUpdatedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ActorConfigUpdatedEventHandler handler) {
        handler.onActorsConfigUpdate();
    }

    public interface ActorConfigUpdatedEventHandler extends EventHandler{
        void onActorsConfigUpdate();
    }
}
