package gov.nist.toolkit.desktop.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Event thrown when another environment is selected.
 * Created by onh2 on 8/30/16.
 */
public class FavoritePidsUpdatedEvent extends GwtEvent<FavoritePidsUpdatedEvent.FavoritePidsUpdatedEventHandler> {

    public static final Type<FavoritePidsUpdatedEventHandler> TYPE = new Type<>();

    /**
     * Event constructor.
     */
    public FavoritePidsUpdatedEvent() {

    }

    @Override
    public Type<FavoritePidsUpdatedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(FavoritePidsUpdatedEventHandler handler) {
        handler.onFavoritePidsUpdated();
    }

    /**
     * Event handler interface for an Environment change (environment selection changed).
     * @see FavoritePidsUpdatedEvent
     */
    public interface FavoritePidsUpdatedEventHandler extends EventHandler {
        /**
         * Actions to be executed on the class that catches the event {@link FavoritePidsUpdatedEvent EnvironmentChangedEvent}
         * when a new environment is selected.
         */
        void onFavoritePidsUpdated();
    }
}
