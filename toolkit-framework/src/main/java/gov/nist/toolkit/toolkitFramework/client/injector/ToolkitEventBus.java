package gov.nist.toolkit.toolkitFramework.client.injector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;

/**
 *
 */
public class ToolkitEventBus extends SimpleEventBus {

    public ToolkitEventBus() {
        GWT.log("In ToolkitEventBus");
    }
}
