package gov.nist.toolkit.xdstools2.client.framework;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import gov.nist.toolkit.xdstools2.client.toolLauncher.ToolLauncher;

/**
 * This is the Activity of the application. It handles the tab opening.
 */
public class ToolActivity extends AbstractActivity {
    private Xdstools2 xdstools2view = Xdstools2.getInstance();
    private String toolId;

    @Override
    public void start(AcceptsOneWidget acceptsOneWidget, EventBus eventBus) {
        // TODO the following can be refactored in a specific method such as openTab
        if(toolId!=null ) {
            Xdstools2.getInstance().doNotDisplayHomeTab();
            // Open required tab
            new ToolLauncher(toolId).launch();
            xdstools2view.resizeToolkit();
        }
    }

    /**
     * Method to set open Tool's ID (or to open)
     * @param toolId
     */
    public void setToolId(String toolId) {
        this.toolId = toolId;
    }

    public Xdstools2 getView(){
        return xdstools2view;
    }
}
