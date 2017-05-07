package gov.nist.toolkit.desktop.client.injection;


import com.google.gwt.core.shared.GWT;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import gov.nist.toolkit.desktop.client.event.TkEventBus;
import gov.nist.toolkit.desktop.client.modules.tool.ToolModule;
import gov.nist.toolkit.desktop.client.modules.tool.ToolPanel;
import gov.nist.toolkit.desktop.client.modules.tool.ToolPresenter;

/**
 *
 */
// Associating the module with the injector
@GinModules(TkGinModule.class)
public interface TkGInjector extends Ginjector {
    TkGInjector INSTANCE = GWT.create(TkGInjector.class);

    TkEventBus getEventBus();

    ToolModule getToolModule();
    ToolPanel getToolPanel();
    ToolPresenter getToolPresenter();
}
