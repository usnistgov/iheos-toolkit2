package gov.nist.toolkit.desktop.client.injection;

import com.google.gwt.core.client.GWT;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import gov.nist.toolkit.desktop.client.*;
import gov.nist.toolkit.desktop.client.environment.EnvironmentMVP;
import gov.nist.toolkit.desktop.client.environment.TestSessionMVP;
import gov.nist.toolkit.desktop.client.events.ToolkitEventBus;
import gov.nist.toolkit.desktop.client.tools.ToolMenu;

import javax.inject.Inject;

/**
 *
 */
public class ToolkitGinModule extends AbstractGinModule {
    @Override
    protected void configure() {
        GWT.log("In ToolkitGinModule");

        bind(ToolkitEventBus.class).in(Singleton.class);

        bind(PlaceController.class).toProvider(PlaceControllerProvider.class).in(Singleton.class);

        bind(ActivityDisplayer.class).to(ActivityDisplayer.ToolkitAppDisplayer.class).in(Singleton.class);

        bind(ToolkitAppView.class).in(Singleton.class);

        bind(TabContainer.class).in(Singleton.class);

        bind(ToolMenu.class).in(Singleton.class);

        bind(EnvironmentMVP.class).in(Singleton.class);

        bind(TestSessionMVP.class).in(Singleton.class);

        bind(ClientUtils.class).in(Singleton.class);

        bind(ServerContext.class).in(Singleton.class);

        // this is only for UI testing along with a hack
        // in EnvironmentService
        // and another in ClientUtils
//        bind(EnvironmentService.class).to(LocalEnvironmentServiceImpl.class).in(Singleton.class);
    }

    /** Provider for PlaceController */
    public static class PlaceControllerProvider implements Provider<PlaceController> {
        @Inject
        ToolkitEventBus eventBus;
        private PlaceController controller;

        @SuppressWarnings("deprecation")
        @Override
        public PlaceController get() {
            if (controller == null) {
                controller = new PlaceController(eventBus);
            }
            return controller;
        }
    }

}
