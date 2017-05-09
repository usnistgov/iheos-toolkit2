package gov.nist.toolkit.xdstools2.client.framework;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import gov.nist.toolkit.xdstools2.client.tabs.FindDocumentsTab;

/**
 * This is the Activity of the application. It handles the tab opening.
 */
public class TestInstanceActivity extends AbstractActivity {
    private Xdstools2 xdstools2view = Xdstools2.getInstance();
    private String tabId;

    @Override
    public void start(AcceptsOneWidget acceptsOneWidget, EventBus eventBus) {
        // TODO the following can be refactored in a specific method such as openTab
        if(tabId!=null ) {
            // Open required tab
            System.out.println("GO TO");
            if (tabId.equals("1234"))
                new FindDocumentsTab().onTabLoad(true, null);
            xdstools2view.resizeToolkit();
        }
    }

    /**
     * Method to set open Tab's ID (or to open)
     * @param tabId
     */
    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    public Xdstools2 getView(){
        return xdstools2view;
    }
}
