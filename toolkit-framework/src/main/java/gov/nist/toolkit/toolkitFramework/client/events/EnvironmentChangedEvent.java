package gov.nist.toolkit.toolkitFramework.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Event thrown when another environment is selected.
 * @see EnvironmentChangedEventHandler
 * Created by onh2 on 8/30/16.
 */
public class EnvironmentChangedEvent extends GwtEvent<EnvironmentChangedEvent.EnvironmentChangedEventHandler> {

    public static final Type<EnvironmentChangedEventHandler> TYPE = new Type<>();
    private final String environment;

    /**
     * Event constructor.
     * @param selectedEnvironment name of the environment selected.
     */
    public EnvironmentChangedEvent(String selectedEnvironment) {
        environment = selectedEnvironment;
    }

    /**
     * @return currently selected environment.
     */
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

    /**
     * Event handler interface for an Environment change (environment selection changed).
     * @see EnvironmentChangedEvent
     */
    public interface EnvironmentChangedEventHandler extends EventHandler {
        /**
         * Actions to be executed on the class that catches the event {@link EnvironmentChangedEvent EnvironmentChangedEvent}
         * when a new environment is selected.
         */
        void onEnvironmentChange(EnvironmentChangedEvent event);
    }
}
