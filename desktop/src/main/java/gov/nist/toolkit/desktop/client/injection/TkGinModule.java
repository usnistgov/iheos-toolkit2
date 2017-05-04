package gov.nist.toolkit.desktop.client.injection;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import gov.nist.toolkit.desktop.client.event.TkEventBus;

/**
 *
 */
public class TkGinModule extends AbstractGinModule {
    @Override
    protected void configure() {

        bind(com.google.web.bindery.event.shared.EventBus.class).to(TkEventBus.class);
        bind(TkEventBus.class).in(Singleton.class);

    }
}
