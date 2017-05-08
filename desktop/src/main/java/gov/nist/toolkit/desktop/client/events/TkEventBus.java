package gov.nist.toolkit.desktop.client.events;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;

import javax.inject.Inject;

/**
 *
 */
public class TkEventBus extends SimpleEventBus {
    @Inject
    private TkEventBus() {
    }

    private static final SimpleEventBus INSTANCE = GWT
            .create(SimpleEventBus.class);

    public static SimpleEventBus get() {
        return INSTANCE;
    }
}
