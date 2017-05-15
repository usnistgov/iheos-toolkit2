package gov.nist.toolkit.xdstools2Framework.client.framework;

import gov.nist.toolkit.toolkitFramework.client.util.MainMenu;
import gov.nist.toolkit.toolkitFramework.client.util.MenuManagement;

import static gov.nist.toolkit.toolkitFramework.client.util.HtmlUtil.addHTML;

/**
 *
 */
public class FrameworkMainMenu implements MainMenu {

    @Override
    public void loadMenu(MenuManagement menuManager) {
        menuManager.addtoMainMenu(addHTML("<h2>Toolkit</h2>"));
    }
}
