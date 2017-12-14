package gov.nist.toolkit.xdstools2.client.abstracts;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.injector.Injector;

/**
 * This is the displayer of the entire application. It enables to make the application more flexible
 * and reduce the amount of code. This way there is only one part of the application that changes through the
 * browser navigation while the rest of the application stays the same and keeps working.
 */
public class ToolkitAppDisplayer implements ActivityDisplayer {
    private HTML titleHtml;

    public ToolkitAppDisplayer() {
        super();
        GWT.log("Build ToolkitAppDisplayer");
    }

    @Override
    public void display(Widget w, String title, AbstractToolkitActivity activity, AbstractPresenter presenter, AcceptsOneWidget p, EventBus b) {
        TabContainer tabContainer = Injector.INSTANCE.getTabContainer();
        GWT.log("ToolkitAppDisplayer:display: " + w.getClass().getName());
        assert(tabContainer != null);

        DockLayoutPanel panel = new DockLayoutPanel(Style.Unit.PX);
        panel.add(w);

        titleHtml = tabContainer.addTab(panel, presenter, title, true /* activity*/);
    }

    @Override
    public void setTitle(String title) {
        titleHtml.setHTML(title);
    }
}
