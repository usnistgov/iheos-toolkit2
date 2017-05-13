package gov.nist.toolkit.xdstools2Framework.client.framework;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.ConfActor;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.SimLog;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.TestInstance;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.Tool;

/**
 * Finds the activity to run for a given Place, used to configure an ActivityManager.
 * It binds the Places with the right Activities.
 *
 * @see TestInstance
 * @see com.google.gwt.activity.shared.ActivityManager
 *
 * Created by onh2 on 9/22/2014.
 */
public class XdsTools2ActivityMapper implements ActivityMapper {
    private static final TkGinInjector INJECTOR = TkGinInjector.INSTANCE;

    public XdsTools2ActivityMapper() {
        super();
    }

    /**
     * This method is supposed to return the right Activity for a given Place to load.
     *
     *
     * @param place Place to load
     * @return the right Activity for a given place to load.
     */
    @Override
    public Activity getActivity(Place place) {
        if (place instanceof TestInstance) {
            TestInstanceActivity testInstanceActivity = INJECTOR.getTestInstanceActivity();
            testInstanceActivity.setTabId(((TestInstance) place).getTabId());
            System.out.println("Go to " + ((TestInstance) place).getTabId());
            return testInstanceActivity;
        }

        if (place instanceof Tool) {
            ToolActivity toolActivity = INJECTOR.getToolActivity();
            toolActivity.setToolId(((Tool) place).getToolId());
            System.out.println("Go to " + ((Tool) place).getToolId());
            return toolActivity;
        }

        if (place instanceof ConfActor) {
            ConfActor confActor = (ConfActor) place;
            ConfActorActivity confActorActivity = INJECTOR.getConfActorActivity();
            confActorActivity.setConfActor(confActor);
            return confActorActivity;
        }

        if (place instanceof SimLog) {
            SimLog simLog = (SimLog) place;
            SimLogActivity simLogActivity = INJECTOR.getSimLogActivity();
            simLogActivity.setSimLog(simLog);
            return simLogActivity;
        }

        // the default - only the shell
        return INJECTOR.getXdsTools2Activity();
    }
}
