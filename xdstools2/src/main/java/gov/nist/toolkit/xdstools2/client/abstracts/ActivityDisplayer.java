package gov.nist.toolkit.xdstools2.client.abstracts;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.xdstools2.client.TabContainer;

import javax.inject.Inject;

/**
 *
 */
public interface ActivityDisplayer {
    void display(Widget w, String title, AbstractToolkitActivity activity, AcceptsOneWidget p, EventBus b);

}
