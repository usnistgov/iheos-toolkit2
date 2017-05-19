package gov.nist.toolkit.desktop.client.framework;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;
import gov.nist.toolkit.desktop.client.toolkit.ToolkitView;
import gov.nist.toolkit.desktop.client.toolkit.ToolkitViewImpl;

/**
 *
 */
public class ClientFactoryImpl implements ClientFactory {
    private final EventBus eventBus = new SimpleEventBus();
    private final PlaceController placeController = new PlaceController(eventBus);
    private final ToolkitViewImpl toolkitView = new ToolkitViewImpl();

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public PlaceController getPlaceController() {
        return placeController;
    }

    @Override
    public ToolkitView getToolkitView() {
        return toolkitView;
    }

}
