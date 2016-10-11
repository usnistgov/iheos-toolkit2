package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.TabBar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
class OptionsTabBar extends TabBar implements SelectionHandler<Integer> {
    // ActorType => list of options
    private static final Map<String, List<String>> actorOptions;
    static {
        actorOptions = new HashMap<>();
        actorOptions.put("ig", java.util.Arrays.asList("Required", "Affinity Domain Option"));
    };

    OptionsTabBar() {
        addSelectionHandler(this);
    }

    void clear() { while(getTabCount() > 0) removeTab(0); }

    void display(String actorTypeId) {
        clear();
        List<String> options = actorOptions.get(actorTypeId);
        if (options == null)
            addTab("Required");
        else {
            for (String option : options) {
                addTab(option);
            }
        }
        selectTab(0);
    }

    @Override
    public void onSelection(SelectionEvent<Integer> selectionEvent) {

    }
};
