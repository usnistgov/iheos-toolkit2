package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.TabBar;

/**
 *
 */
class ActorTabBar extends TabBar implements SelectionHandler<Integer> {

    ActorTabBar() {
        addSelectionHandler(this);
    }

    @Override
    public void onSelection(SelectionEvent<Integer> selectionEvent) {

    }
}
