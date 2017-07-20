package gov.nist.toolkit.xdstools2.client.abstracts;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import gov.nist.toolkit.xdstools2.client.TabContainer;

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

        @Inject
        private TabContainer tabContainer;

        public ToolkitAppDisplayer() {}

        @Override
        public void display(Widget w, String title, AbstractToolkitActivity activity, AcceptsOneWidget p, EventBus b) {
            GWT.log("ToolkitAppDisplayer:display: " + w.getClass().getName());
            assert(tabContainer != null);

            DockLayoutPanel panel = new DockLayoutPanel(Style.Unit.PX);
            panel.add(w);

            tabContainer.addTab(panel, title, true /* activity*/);
        }
    }
}
