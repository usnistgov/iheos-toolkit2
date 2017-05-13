package gov.nist.toolkit.toolkitFramework.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 *
 */
public class TabOpenedEvent extends GwtEvent<TabOpenedEvent.TabOpenedEventHandler> {
    public static final Type<TabOpenedEventHandler> TYPE = new GwtEvent.Type<>();
    private final String title;
    private final int index;

    public TabOpenedEvent(String title, int index) { this.title = title; this.index = index; }

    public String getTitle() { return title; }
    public int getIndex() { return index; }

    @Override
    public Type<TabOpenedEventHandler> getAssociatedType() { return TYPE; }

    @Override
    protected void dispatch(TabOpenedEventHandler tabOpenedEventHandler) {
        tabOpenedEventHandler.onTabOpened(this);
    }

    public interface TabOpenedEventHandler extends EventHandler {
        void onTabOpened(TabOpenedEvent event);
    }
}
