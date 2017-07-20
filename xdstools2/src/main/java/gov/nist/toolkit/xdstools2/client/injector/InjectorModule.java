package gov.nist.toolkit.xdstools2.client.injector;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import gov.nist.toolkit.xdstools2.client.TabContainer;

/**
 *
 */
public class InjectorModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
        bind(TabContainer.class).in(Singleton.class);
    }
}
