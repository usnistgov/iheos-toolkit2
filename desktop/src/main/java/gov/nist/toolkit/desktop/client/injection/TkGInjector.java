package gov.nist.toolkit.desktop.client.injection;


import com.google.gwt.core.shared.GWT;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.google.gwt.place.shared.PlaceController;
import gov.nist.toolkit.desktop.client.events.TkEventBus;
import gov.nist.toolkit.desktop.client.toolkit.ToolkitActivity;
import gov.nist.toolkit.desktop.client.toolkit.ToolkitViewImpl;

/**
 *
 */
// Associating the module with the injector
@GinModules(TkGinModule.class)
public interface TkGInjector extends Ginjector {
    TkGInjector INSTANCE = GWT.create(TkGInjector.class);

    TkEventBus getEventBus();

    ToolkitViewImpl getContentPanel();

    PlaceController getPlaceController();

    ToolkitActivity getToolkitActivity();


//    ToolModule getToolModule();
//    ToolPanel getToolPanel();
//    ToolPresenter getToolPresenter();
}
