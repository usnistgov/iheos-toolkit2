package gov.nist.toolkit.xdstools2Framework.client.framework;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import javax.inject.Inject;

/**
 *
 */
public interface ActivityDisplayer {
    public void display(Widget w, AcceptsOneWidget p, EventBus b);

    /**
     * This is the displayer of the Metadata editor application. It enables to make the application more flexible
     * and reduce the amount of code. This way there is only one part of the application that changes through the
     * browser navigation while the rest of the application stays the same and keeps working.
     */
    public class XdsTools2AppDisplayer implements ActivityDisplayer {
        @Inject
        private XdsTools2AppView appView;

        public XdsTools2AppDisplayer() {}

        @Override
        public void display(Widget w, AcceptsOneWidget p, EventBus b) {
            // not sure what needs to be done here
        }
    }
}
