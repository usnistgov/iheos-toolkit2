package gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs;

import com.google.gwt.event.shared.EventHandler;

/**
 *
 */
public interface ServiceCallCompletionHandler<T> extends EventHandler {
    void onCompletion(T var);
}
