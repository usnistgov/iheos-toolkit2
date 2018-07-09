package gov.nist.toolkit.xdstools2.client.event;



import com.google.gwt.event.shared.EventHandler;

public interface ToolkitInitializationCompleteEventHandler extends EventHandler {
    void onInitialized(ToolkitInitializationCompleteEvent event);
}
