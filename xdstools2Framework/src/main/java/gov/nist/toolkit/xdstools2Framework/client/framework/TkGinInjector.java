package gov.nist.toolkit.xdstools2Framework.client.framework;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.google.gwt.place.shared.PlaceController;
import gov.nist.toolkit.xdstools2.client.event.Xdstools2EventBus;
import gov.nist.toolkit.xdstools2.client.util.ToolkitServiceAsync;

/**
 *
 */
// Associating the module with the injector
@GinModules(TkGinModule.class)
public interface TkGinInjector extends Ginjector {
    TkGinInjector INSTANCE = GWT.create(TkGinInjector.class);

    Xdstools2EventBus getEventBus();
    PlaceController getPlaceController();

    ToolkitServiceAsync getToolkitServices();

    XdsTools2AppView getXdsTools2AppView();
    XdsTools2Presenter getXdsTools2AppPresenter();
    XdsTools2Activity getXdsTools2Activity();

    TestInstanceActivity getTestInstanceActivity();
    ToolActivity getToolActivity();
    ConfActorActivity getConfActorActivity();
    SimLogActivity getSimLogActivity();
}
