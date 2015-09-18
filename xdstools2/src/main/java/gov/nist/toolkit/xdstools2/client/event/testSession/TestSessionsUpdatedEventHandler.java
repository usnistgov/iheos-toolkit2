package gov.nist.toolkit.xdstools2.client.event.testSession;

import com.google.gwt.event.shared.EventHandler;

/**
 * Created by bill on 9/16/15.
 */
public interface TestSessionsUpdatedEventHandler extends EventHandler {
    void onTestSessionsUpdated(TestSessionsUpdatedEvent event);
}
