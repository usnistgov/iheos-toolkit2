package gov.nist.toolkit.desktop.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 *
 */
public class ResizeToolkitEvent extends GwtEvent<ResizeToolkitEvent.ResizeToolkitEventHandler> {


    @Override
    public Type<ResizeToolkitEventHandler> getAssociatedType() {
        return null;
    }

    @Override
    protected void dispatch(ResizeToolkitEventHandler resizeToolkitEventHandler) {

    }

    public interface ResizeToolkitEventHandler extends EventHandler {
        void onToolkitResize();
    }}
