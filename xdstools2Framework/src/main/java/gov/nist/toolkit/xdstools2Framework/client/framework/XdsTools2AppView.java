package gov.nist.toolkit.xdstools2Framework.client.framework;

import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.xdstools2.client.toolLauncher.ToolLauncher;

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

    void addToMainMenu(Hyperlink hyperlink, ToolLauncher toolLauncher);

    void addToMainMenu(HTML html);
}
