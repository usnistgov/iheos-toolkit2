package gov.nist.toolkit.toolkitFramework.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 *
 */
public class SystemsNeedReloadingEvent extends GwtEvent<SystemsNeedReloadingEvent.SystemsNeedReloadingEventHandler> {

    public static final Type<SystemsNeedReloadingEventHandler> TYPE = new Type<>();

    @Override
    public Type<SystemsNeedReloadingEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SystemsNeedReloadingEventHandler handler) {
        handler.onSystemsNeedReloading();
    }

    public interface SystemsNeedReloadingEventHandler extends EventHandler {
        void onSystemsNeedReloading();
    }
}
