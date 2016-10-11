package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.user.client.ui.TabBar;

import java.util.List;
import java.util.Map;

/**
 *
 */
class OptionsTabBar extends TabBar  {
    private Map<String, List<ActorAndOptions>> actorOptions;

    OptionsTabBar(Map<String, List<ActorAndOptions>> actorOptions) {
        this.actorOptions =actorOptions;
    }

    void clear() { while(getTabCount() > 0) removeTab(0); }

    void display(String actorTypeId) {
        clear();
        List<ActorAndOptions> options = actorOptions.get(actorTypeId);
        if (options == null)
            addTab("Required");
        else {
            for (ActorAndOptions option : options) {
                addTab(option.getOptionTitle());
            }
        }
        selectTab(0);
    }

};
