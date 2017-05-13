package gov.nist.toolkit.toolkitFramework.client.injector;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import gov.nist.toolkit.toolkitFramework.client.environment.EnvironmentState;
import gov.nist.toolkit.toolkitFramework.client.environment.EnvironmentStateImpl;
import gov.nist.toolkit.toolkitFramework.client.service.FrameworkServiceAsync;

/**
 *
 */
public class FrameworkGinModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);

        bind(FrameworkServiceAsync.class).in(Singleton.class);
        bind(EnvironmentState.class).to(EnvironmentStateImpl.class).in(Singleton.class);
    }
}
