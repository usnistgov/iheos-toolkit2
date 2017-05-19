package gov.nist.toolkit.desktop.client.injection;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.google.gwt.place.shared.PlaceController;
import gov.nist.toolkit.desktop.client.ToolkitAppView;
import gov.nist.toolkit.desktop.client.events.ToolkitEventBus;
import gov.nist.toolkit.desktop.client.home.WelcomeActivity;
import gov.nist.toolkit.desktop.client.tools.toy.ToyActivity;

/**
 *
 */
// Associating the module with the injector
@GinModules({ToolkitGinModule.class})
public interface ToolkitGinInjector extends Ginjector {
    ToolkitGinInjector INSTANCE = GWT.create(ToolkitGinInjector.class);

    ToolkitEventBus getEventBus();

    ToolkitAppView getToolkitAppView();

    WelcomeActivity getWelcomeActivity();
    ToyActivity getToyActivity();

    PlaceController getPlaceController();

//    XdsTools2AppView getXdsTools2AppView();
//    XdsTools2Presenter getXdsTools2AppPresenter();
//    XdsTools2Activity getXdsTools2Activity();
//
//    TestInstanceActivity getTestInstanceActivity();
//    ToolActivity getToolActivity();
//    ConfActorActivity getConfActorActivity();
//    SimLogActivity getSimLogActivity();

//    EnvironmentState getEnvironmentState();
//    TestSessionManager getTestSessionManager();
}
