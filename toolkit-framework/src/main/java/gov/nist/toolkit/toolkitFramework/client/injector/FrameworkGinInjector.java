package gov.nist.toolkit.toolkitFramework.client.injector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import gov.nist.toolkit.toolkitFramework.client.environment.EnvironmentState;
import gov.nist.toolkit.toolkitFramework.client.service.FrameworkServiceAsync;

/**
 *
 */
@GinModules(FrameworkGinModule.class)
public interface FrameworkGinInjector extends Ginjector {
    FrameworkGinInjector INSTANCE = GWT.create(FrameworkGinInjector.class);
    public EventBus getEventBus();
    public FrameworkServiceAsync getFrameworkServices();
    public EnvironmentState getEnvironmentState();

}
