package gov.nist.toolkit.xdstools2.client.event;


import com.google.gwt.event.shared.GwtEvent;

public class ToolkitInitializationCompleteEvent extends GwtEvent<ToolkitInitializationCompleteEventHandler> {

    public static final Type<ToolkitInitializationCompleteEventHandler> TYPE = new Type<ToolkitInitializationCompleteEventHandler>();

    @Override
    public Type<ToolkitInitializationCompleteEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ToolkitInitializationCompleteEventHandler handler) {
        handler.onInitialized(this);
    }


}



