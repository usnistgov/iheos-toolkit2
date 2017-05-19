package gov.nist.toolkit.desktop.client.framework;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import gov.nist.toolkit.desktop.client.toolkit.ToolkitView;

/**
 *
 */
public interface ClientFactory {
    EventBus getEventBus();
    PlaceController getPlaceController();
    ToolkitView getToolkitView();
}
