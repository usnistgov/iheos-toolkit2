package gov.nist.toolkit.xdstools2Framework.client.framework;

import com.google.gwt.core.client.GWT;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import gov.nist.toolkit.toolkitFramework.client.injector.ToolkitEventBus;
import gov.nist.toolkit.toolkitFramework.client.testSession.TestSessionManager;

import javax.inject.Inject;

/**
 *
 */
public class TkGinModule extends AbstractGinModule {
    @Override
    protected void configure() {
        GWT.log("In TkGinModule");

        bind(com.google.gwt.place.shared.PlaceController.class).toProvider(PlaceControllerProvider.class).in(Singleton.class);

        bind(ActivityDisplayer.class).to(ActivityDisplayer.XdsTools2AppDisplayer.class).in(Singleton.class);

        bind(TestSessionManager.class).in(Singleton.class);

        bind(XdsTools2AppView.class).in(Singleton.class);

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
