package gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import gov.nist.toolkit.xdstools2.client.Xdstools2;
import gov.nist.toolkit.xdstools2.client.toolLauncher.ToolLauncher;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.toolContext.State;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.toolContext.Token;

/**
 * This is the Activity of the application. It handles the tab opening.
 */
public class ToolActivity extends AbstractActivity {
    private Xdstools2 xdstools2view = Xdstools2.getInstance();
    State state;

    @Override
    public void start(AcceptsOneWidget acceptsOneWidget, EventBus eventBus) {
        // TODO the following can be refactored in a specific method such as openTab
        if(state!=null && state.getValue(Token.TOOLID)!=null) {
            Xdstools2.getInstance().doNotDisplayHomeTab();
            // Open required tab
            new ToolLauncher(state).launch();
            xdstools2view.resizeToolkit();
        }
    }

    public void setState(State state) {
        this.state = state;
    }

    public Xdstools2 getView(){
        return xdstools2view;
    }
}
