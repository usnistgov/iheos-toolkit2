package gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;
import gov.nist.toolkit.xdstools2.client.Xdstools2;
import gov.nist.toolkit.xdstools2.client.toolLauncher.ToolLauncher;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.toolContext.ToolParameter;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.toolContext.ToolParameterMap;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;

/**
 * This is the Activity of the application. It handles the tab opening.
 */
public class ToolActivity extends AbstractActivity {
    private Xdstools2 xdstools2view = Xdstools2.getInstance();
    private ToolParameterMap tpm;

    @Override
    public void start(AcceptsOneWidget acceptsOneWidget, EventBus eventBus) {
        // TODO the following can be refactored in a specific method such as openTab
        if(tpm!=null && tpm.getValue(ToolParameter.TOOLID)!=null) {
            Xdstools2.getInstance().doNotDisplayHomeTab();
            // Open required tab
            try {
                ToolLauncher launcher = new ToolLauncher(tpm.getValue(ToolParameter.TOOLID));
                launcher.setTpm(tpm);
                launcher.launch();
            } catch (ToolkitRuntimeException tre) {
                new PopupMessage(tre.toString());
            }
            xdstools2view.resizeToolkit();
        }
    }

    public void setTpm(ToolParameterMap tpm) {
        this.tpm = tpm;
    }

    public Xdstools2 getView(){
        return xdstools2view;
    }
}
