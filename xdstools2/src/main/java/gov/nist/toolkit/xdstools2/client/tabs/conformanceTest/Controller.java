package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.event.dom.client.ClickHandler;

/**
 *
 */
public interface Controller {
    ClickHandler getRunAllClickHandler();
    ClickHandler getDeleteAllClickHandler();
    ClickHandler getRefreshTestCollectionClickHandler();
}

