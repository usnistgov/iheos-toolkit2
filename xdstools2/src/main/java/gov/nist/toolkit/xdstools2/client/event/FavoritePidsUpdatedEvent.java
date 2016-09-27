package gov.nist.toolkit.xdstools2.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by onh2 on 9/27/16.
 */
public class FavoritePidsUpdatedEvent extends GwtEvent<FavoritePidsUpdatedEvent.FavoritePidsUpdatedEventHandler> {
    public static final Type<FavoritePidsUpdatedEventHandler> TYPE = new Type<FavoritePidsUpdatedEventHandler>();
    @Override
    public Type<FavoritePidsUpdatedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(FavoritePidsUpdatedEventHandler handler) {
        handler.onFavPidsUpdate();
    }

    public interface FavoritePidsUpdatedEventHandler extends EventHandler {
        void onFavPidsUpdate();
    }
}
