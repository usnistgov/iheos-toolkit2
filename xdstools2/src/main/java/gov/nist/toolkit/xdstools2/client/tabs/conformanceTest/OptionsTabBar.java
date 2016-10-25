package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.user.client.ui.TabBar;

import java.util.List;

/**
 *
 */
class OptionsTabBar extends TabBar  {

    void clear() { while(getTabCount() > 0) removeTab(0); }

    void display(String actorTypeId) {
        clear();
        List<String> optionTitles = ActorOptionManager.optionTitles(actorTypeId);
        if (optionTitles.isEmpty())
            addTab("Required");
        else {
            for (String title : optionTitles) {
                addTab(title);
            }
        }
        selectTab(0);
    }

};
