package gov.nist.toolkit.desktop.client;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import gov.nist.toolkit.desktop.client.abstracts.AbstractToolkitActivity;

import javax.inject.Inject;

/**
 *
 */
public interface ActivityDisplayer {
    public void display(Widget w, String title, AbstractToolkitActivity activity, AcceptsOneWidget p, EventBus b);

    /**
     * This is the displayer of the entire application. It enables to make the application more flexible
     * and reduce the amount of code. This way there is only one part of the application that changes through the
     * browser navigation while the rest of the application stays the same and keeps working.
     */
    class ToolkitAppDisplayer implements ActivityDisplayer {
        private static int counter = 0;

        @Inject
        private TabContainer tabContainer;

        public ToolkitAppDisplayer() {}

        @Override
        public void display(Widget w, String title, AbstractToolkitActivity activity, AcceptsOneWidget p, EventBus b) {
            GWT.log("ToolkitAppDisplayer:display: " + w.getClass().getName());
            if (title.equals("Welcome")) {
                counter++;
                assert(counter != 2);  // if this gets redisplayed then something is broken with places
            }
            tabContainer.addTab(w, title, activity);
        }
    }
}
