package gov.nist.toolkit.desktop.client.injection;


import com.google.gwt.core.shared.GWT;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import gov.nist.toolkit.desktop.client.event.TkEventBus;

/**
 *
 */
// Associating the module with the injector
@GinModules(TkGinModule.class)
public interface TkGInjector extends Ginjector {
    TkGInjector INSTANCE = GWT.create(TkGInjector.class);

    TkEventBus getEventBus();
}
