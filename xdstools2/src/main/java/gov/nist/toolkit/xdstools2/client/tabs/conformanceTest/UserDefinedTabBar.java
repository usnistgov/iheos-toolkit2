package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.user.client.ui.TabBar;

/**
 *
 */
class UserDefinedTabBar extends TabBar  {

    void clear() { while(getTabCount() > 0) removeTab(0); }


    /**
     *
     * @param tabConfig document root
     * @param targetTabGroupType
     * @param parentTabCode
     */
    void display(TabConfig tabConfig, String targetTabGroupType, String parentTabCode) {

        for (TabConfig child : tabConfig.getChildTabConfigs()) {
           if (parentTabCode.equals(child.getTcCode()))  {
              TabConfig subTabConfig = child.getFirstChildTabConfig();
              if (targetTabGroupType.equals(subTabConfig.getLabel())) {
                 for (TabConfig targetTab : subTabConfig.getChildTabConfigs())  {
                    addTab(targetTab.getLabel());
                 }
              }
           } else if (child.hasChildren()) {
              display(child, targetTabGroupType, parentTabCode);
           }
        }
    }


};
