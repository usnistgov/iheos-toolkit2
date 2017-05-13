package gov.nist.toolkit.xdstools2Framework.client.framework;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

/**
 * This activity is used to launch all tools within toolkit
 */
public class ToolActivity extends AbstractActivity {
    private XdsTools2AppView xdstools2view = XdsTools2AppViewImpl.getInstance();
    private String toolId;

    @Override
    public void start(AcceptsOneWidget acceptsOneWidget, EventBus eventBus) {
        assert(false);
        // TODO the following can be refactored in a specific method such as openTab
        if(toolId!=null ) {
            // Open required tab
//            new ToolLauncher(toolId).launch();
//            xdstools2view.resizeToolkit();
        }
    }

    /**
     * Method to set open Tool's ID (or to open)
     * @param toolId
     */
    public void setToolId(String toolId) {
        this.toolId = toolId;
    }

    public XdsTools2AppView getView(){
        return xdstools2view;
    }
}
