package gov.nist.toolkit.desktop.client.events;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 *
 */
public class ToolkitEventBus extends SimpleEventBus {

    public ToolkitEventBus() {
        GWT.log("In ToolkitEventBus");
    }
}
