package gov.nist.toolkit.toolkitFramework.client.events;


import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Event thrown when an ActorConfig is changed (creation, deletion, modification).
 * @see ActorConfigUpdatedEventHandler
 * Created by onh2 on 9/20/16.
 */
public class ActorConfigUpdatedEvent extends GwtEvent<ActorConfigUpdatedEvent.ActorConfigUpdatedEventHandler> {
    public static final Type<ActorConfigUpdatedEventHandler> TYPE = new Type<>();

    @Override
    public Type<ActorConfigUpdatedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ActorConfigUpdatedEventHandler handler) {
        handler.onActorsConfigUpdate();
    }

    /**
     * Event handler interface for an ActorConfig update.
     * @see ActorConfigUpdatedEvent
     */
    public interface ActorConfigUpdatedEventHandler extends EventHandler{
        /**
         * Actions to be executed on the class that catches the event when there is
         * an actor update (creation, deletion, modification).
         */
        void onActorsConfigUpdate();
    }
}
