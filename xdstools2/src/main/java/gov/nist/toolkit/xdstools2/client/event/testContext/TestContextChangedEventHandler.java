package gov.nist.toolkit.xdstools2.client.event.testContext;

import com.google.gwt.event.shared.EventHandler;

/**
 * Created by skb1 on 6/27/17.
 */
public interface TestContextChangedEventHandler extends EventHandler {

    void onTestContextChanged(TestContextChangedEvent event);
}
