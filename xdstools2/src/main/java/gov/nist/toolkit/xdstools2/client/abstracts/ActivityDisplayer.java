package gov.nist.toolkit.xdstools2.client.abstracts;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 */
public interface ActivityDisplayer {
    void display(Widget w, String title, AbstractToolkitActivity activity, AbstractPresenter presenter, AcceptsOneWidget p, EventBus b);
    void setTitle(String title);

}
