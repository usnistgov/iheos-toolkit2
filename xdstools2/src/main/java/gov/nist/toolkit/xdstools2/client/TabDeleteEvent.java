package gov.nist.toolkit.xdstools2.client;

import com.google.gwt.event.shared.GwtEvent;

public class TabDeleteEvent extends GwtEvent<TabDeleteEventHandler> {
    public static final Type<TabDeleteEventHandler> TYPE = new Type<>();

    public TabDeleteEvent() {

    }

    @Override
    public Type<TabDeleteEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(TabDeleteEventHandler tabDeleteEventHandler) {
        tabDeleteEventHandler.onTabDelete(this);
    }
}
