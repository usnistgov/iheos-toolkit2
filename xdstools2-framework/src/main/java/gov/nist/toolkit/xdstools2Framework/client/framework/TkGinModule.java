package gov.nist.toolkit.xdstools2Framework.client.framework;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import gov.nist.toolkit.toolkitFramework.client.util.MenuManagement;

import javax.inject.Inject;

/**
 *
 */
public class TkGinModule extends AbstractGinModule {
    @Override
    protected void configure() {
        GWT.log("In TkGinModule");

        bind(MenuManagement.class).to(XdsTools2AppView.class);

        bind(com.google.gwt.place.shared.PlaceController.class).toProvider(PlaceControllerProvider.class).in(Singleton.class);

        bind(ActivityDisplayer.class).to(ActivityDisplayer.XdsTools2AppDisplayer.class).in(Singleton.class);

//        bind(ToolkitServiceAsync.class).in(Singleton.class);


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
