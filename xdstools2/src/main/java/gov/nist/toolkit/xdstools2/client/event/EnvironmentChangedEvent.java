package gov.nist.toolkit.xdstools2.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by onh2 on 8/30/16.
 */
public class EnvironmentChangedEvent extends GwtEvent<EnvironmentChangedEvent.EnvironmentChangedEventHandler> {

    public static final Type<EnvironmentChangedEventHandler> TYPE = new Type<EnvironmentChangedEventHandler>();
    private final String environment;

    public EnvironmentChangedEvent(String selectedEnvironment) {
        environment = selectedEnvironment;
    }

    public String getEnvironment(){
        return environment;
    }

    @Override
    public Type<EnvironmentChangedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(EnvironmentChangedEventHandler handler) {
        handler.onEnvironmentChange(this);
    }

    public interface EnvironmentChangedEventHandler extends EventHandler {
        void onEnvironmentChange(EnvironmentChangedEvent event);
    }
}
