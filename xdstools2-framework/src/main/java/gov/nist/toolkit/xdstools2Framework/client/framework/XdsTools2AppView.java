package gov.nist.toolkit.xdstools2Framework.client.framework;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 */
public interface XdsTools2AppView extends AcceptsOneWidget, IsWidget {
    @Override
    void setWidget(IsWidget isWidget);

    @Override
    Widget asWidget();

    void buildTabsWrapper();

    void clearMainMenu();

    void addtoMainMenu(Widget w);
}
