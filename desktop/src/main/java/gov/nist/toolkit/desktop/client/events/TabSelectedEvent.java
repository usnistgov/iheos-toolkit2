package gov.nist.toolkit.desktop.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Event thrown when a tab that is open already is selected again.
 * @see TabSelectedEventHandler
 *
 */
public class TabSelectedEvent extends GwtEvent<TabSelectedEvent.TabSelectedEventHandler> {
    public static final Type<TabSelectedEventHandler> TYPE=new Type<>();
    private final String tabName;

    public TabSelectedEvent(String tabName) {
        this.tabName=tabName;
    }

    public String getTabName(){
        return this.tabName;
    }

    @Override
    public Type<TabSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(TabSelectedEventHandler handler) {
        handler.onTabSelection(this);
    }

    /**
     * Event handler to used with {@link TabSelectedEvent}
     */
    public interface TabSelectedEventHandler extends EventHandler {
        void onTabSelection(TabSelectedEvent event);
    }
}
