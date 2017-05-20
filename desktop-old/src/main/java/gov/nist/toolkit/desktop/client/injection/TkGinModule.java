package gov.nist.toolkit.desktop.client.injection;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import gov.nist.toolkit.desktop.client.events.TkEventBus;

/**
 *
 */
public class TkGinModule extends AbstractGinModule {
    @Override
    protected void configure() {

        bind(com.google.web.bindery.event.shared.EventBus.class).to(TkEventBus.class);
        bind(TkEventBus.class).in(Singleton.class);

        bind(com.google.gwt.place.shared.PlaceController.class).toProvider(PlaceControllerProvider.class).in(Singleton.class);

//        bind(ToolkitViewImpl.class).in(Singleton.class);

    }

    /** Provider for PlaceController */
    public static class PlaceControllerProvider implements Provider<PlaceController> {
        @Inject
        TkEventBus eventBus;
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
