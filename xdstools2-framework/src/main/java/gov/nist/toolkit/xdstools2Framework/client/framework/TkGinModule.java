package gov.nist.toolkit.xdstools2Framework.client.framework;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import javax.inject.Inject;

/**
 *
 */
public class TkGinModule extends AbstractGinModule {
    @Override
    protected void configure() {

//        bind(com.google.web.bindery.event.shared.EventBus.class).to(Xdstools2EventBus.class);
//        bind(Xdstools2EventBus.class).in(Singleton.class);

        bind(com.google.gwt.place.shared.PlaceController.class).toProvider(PlaceControllerProvider.class).in(Singleton.class);

//        bind(ToolkitServiceAsync.class).in(Singleton.class);

//        bind(TestSessionManager.class).to(TestSessionManager2.class).in(Singleton.class);
//
//        bind(FrameworkSupport.class).to(OldFrameworkSupport.class).in(Singleton.class);
//
//        bind(EnvironmentState.class).to(EnvironmentStateImpl.class).in(Singleton.class);

        bind(XdsTools2AppView.class).to(XdsTools2AppViewImpl.class).in(Singleton.class);

    }

    /** Provider for PlaceController */
    public static class PlaceControllerProvider implements Provider<PlaceController> {
        @Inject
        EventBus eventBus;
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
