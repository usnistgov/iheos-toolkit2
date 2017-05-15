package gov.nist.toolkit.xdstools2Framework.client.framework;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.place.shared.PlaceController;
import gov.nist.toolkit.toolkitFramework.client.environment.EnvironmentState;
import gov.nist.toolkit.toolkitFramework.client.injector.FrameworkGinInjector;
import gov.nist.toolkit.toolkitFramework.client.injector.FrameworkGinModule;
import gov.nist.toolkit.toolkitFramework.client.testSession.TestSessionManager;

/**
 *
 */
// Associating the module with the injector
@GinModules({FrameworkGinModule.class, TkGinModule.class})
public interface TkGinInjector extends FrameworkGinInjector {
    TkGinInjector INSTANCE = GWT.create(TkGinInjector.class);

    PlaceController getPlaceController();

    XdsTools2AppView getXdsTools2AppView();
    XdsTools2Presenter getXdsTools2AppPresenter();
    XdsTools2Activity getXdsTools2Activity();

    TestInstanceActivity getTestInstanceActivity();
    ToolActivity getToolActivity();
    ConfActorActivity getConfActorActivity();
    SimLogActivity getSimLogActivity();

    EnvironmentState getEnvironmentState();
    TestSessionManager getTestSessionManager();
}
