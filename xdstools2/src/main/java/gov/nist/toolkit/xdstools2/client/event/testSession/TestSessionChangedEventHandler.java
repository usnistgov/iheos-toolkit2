package gov.nist.toolkit.xdstools2.client.event.testSession;

import com.google.gwt.event.shared.EventHandler;
import gov.nist.toolkit.xdstools2.client.event.TestSessionChangedEvent;

/**
 * Created by bill on 9/16/15.
 */
public interface TestSessionChangedEventHandler extends EventHandler {

    void onTestSessionChanged(TestSessionChangedEvent event);
}
