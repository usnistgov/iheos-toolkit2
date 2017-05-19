package gov.nist.toolkit.desktop.client;

import com.google.gwt.core.client.GWT;
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
     * This is the displayer of the entire application. It enables to make the application more flexible
     * and reduce the amount of code. This way there is only one part of the application that changes through the
     * browser navigation while the rest of the application stays the same and keeps working.
     */
    public class ToolkitAppDisplayer implements ActivityDisplayer {
        @Inject
        private ToolkitAppView appView;  // this will be a singleton

        public ToolkitAppDisplayer() {}

        @Override
        public void display(Widget w, AcceptsOneWidget p, EventBus b) {
            GWT.log("ToolkitAppDisplayer:display: " + w.getClass().getName());
            appView.setWidget(w);
        }
    }
}
