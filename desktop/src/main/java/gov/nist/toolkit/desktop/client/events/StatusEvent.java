package gov.nist.toolkit.desktop.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.web.bindery.event.shared.Event;

/**
 *
 */
public class StatusEvent extends Event<StatusEvent.StatusHandler> {

    public interface StatusHandler extends EventHandler {
        public void onStatusChange(StatusEvent event);
    }

    public static final Type<StatusHandler> TYPE = new Type<StatusHandler>();

    private String status;

    public StatusEvent(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public Type<StatusHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(StatusHandler handler) {
        handler.onStatusChange(this);
    }}
