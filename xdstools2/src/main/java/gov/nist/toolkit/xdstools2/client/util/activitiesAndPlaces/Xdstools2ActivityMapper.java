package gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import gov.nist.toolkit.xdstools2.client.util.ClientFactory;

/**
 * Finds the activity to run for a given Place, used to configure an ActivityManager.
 * It binds the Places with the right Activities.
 *
 * @see TestInstance
 * @see com.google.gwt.activity.shared.ActivityManager
 *
 * Created by onh2 on 9/22/2014.
 */
public class Xdstools2ActivityMapper implements ActivityMapper {
    private ClientFactory clientFactory;

    public Xdstools2ActivityMapper(ClientFactory clientFactory) {
        super();
        this.clientFactory=clientFactory;
    }

    /**
     * This method is supposed to return the right Activity for a given Place to load.
     * It sets the id of the tab to load in TestInstanceActivity and return the Activity.
     * The Activity will use this id to know which tab to open when it starts.
     *
     * @param place Place to load
     * @return the right Activity for a given place to load.
     */
    @Override
    public Activity getActivity(Place place) {
        if (place instanceof TestInstance) {
            TestInstanceActivity testInstanceActivity = clientFactory.getTestInstanceActivity();
            testInstanceActivity.setTabId(((TestInstance) place).getTabId());
            System.out.println("Go to " + ((TestInstance) place).getTabId());
            return testInstanceActivity;
        }

        if (place instanceof  Tool) {
            ToolActivity toolActivity = clientFactory.getToolActivity();
            toolActivity.setToolId(((Tool) place).getToolId());
            System.out.println("Go to " + ((Tool) place).getToolId());
            return toolActivity;
        }

        if (place instanceof ConfActor) {
            ConfActor confActor = (ConfActor) place;
            ConfActorActivity confActorActivity = clientFactory.getConfActorActivity();
            confActorActivity.setConfActor(confActor);
            return confActorActivity;
        }
        return null;
    }
}
