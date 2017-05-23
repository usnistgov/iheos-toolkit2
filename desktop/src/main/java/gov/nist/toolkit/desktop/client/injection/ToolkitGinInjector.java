package gov.nist.toolkit.desktop.client.injection;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.google.gwt.place.shared.PlaceController;
import gov.nist.toolkit.desktop.client.ClientUtils;
import gov.nist.toolkit.desktop.client.ServerContext;
import gov.nist.toolkit.desktop.client.TabContainer;
import gov.nist.toolkit.desktop.client.ToolkitAppView;
import gov.nist.toolkit.desktop.client.environment.EnvironmentMVP;
import gov.nist.toolkit.desktop.client.environment.TestSessionMVP;
import gov.nist.toolkit.desktop.client.events.ToolkitEventBus;
import gov.nist.toolkit.desktop.client.home.WelcomeActivity;
import gov.nist.toolkit.desktop.client.tools.ToolMenu;
import gov.nist.toolkit.desktop.client.tools.getDocuments.GetDocumentsActivity;
import gov.nist.toolkit.desktop.client.tools.toy.ToyActivity;

/**
 *
 */
// Associating the module with the injector
@GinModules({ToolkitGinModule.class})
public interface ToolkitGinInjector extends Ginjector {
    ToolkitGinInjector INSTANCE = GWT.create(ToolkitGinInjector.class);

    ToolkitEventBus getEventBus();

    ToolMenu getToolMenu();

    ToolkitAppView getToolkitAppView();

    TabContainer getTabContainer();

    WelcomeActivity getWelcomeActivity();
    ToyActivity getToyActivity();

    PlaceController getPlaceController();

    ClientUtils getClientUtils();

    EnvironmentMVP getEnvironmentMVP();
    TestSessionMVP getTestSessionMVP();

    GetDocumentsActivity getGetDocumentsActivity();

    ServerContext getServerContext();
}
