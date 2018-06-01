package gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import gov.nist.toolkit.xdstools2.client.Xdstools2;
import gov.nist.toolkit.xdstools2.client.inspector.mvp.ResultInspector;
import gov.nist.toolkit.xdstools2.client.tabs.SubmitResourceTab.SubmitResource;
import gov.nist.toolkit.xdstools2.client.tabs.fhirSearchTab.FhirSearch;
import gov.nist.toolkit.xdstools2.client.tabs.simMsgViewerTab.SimMsgViewer;
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
     *
     *
     * @param place Place to load
     * @return the right Activity for a given place to load.
     */
    @Override
    public Activity getActivity(Place place) {
        GWT.log("Xdstools2ActivityMapper - place is of type " + place.getClass().getName() );
        if (place instanceof TestInstance) {
            TestInstanceActivity testInstanceActivity = clientFactory.getTestInstanceActivity();
            testInstanceActivity.setTabId(((TestInstance) place).getTabId());
            System.out.println("Go to " + ((TestInstance) place).getTabId());
            pushHomeTabToBackground();
            return testInstanceActivity;
        }

        if (place instanceof  Tool) {
            Tool tool = (Tool)place;
            ToolActivity toolActivity = clientFactory.getToolActivity();
            toolActivity.setState(tool.getState());
            System.out.println("Go to " + ((Tool) place).getToolId());
            pushHomeTabToBackground();
            return toolActivity;
        }

        if (place instanceof ConfActor) {
            ConfActor confActor = (ConfActor) place;
            ConfActorActivity confActorActivity = clientFactory.getConfActorActivity();
            confActorActivity.setConfActor(confActor);
            pushHomeTabToBackground();
            return confActorActivity;
        }

        if (place instanceof SimLog) {
            SimLog simLog = (SimLog) place;
            SimLogActivity simLogActivity = clientFactory.getSimLogActivity();
            simLogActivity.setSimLog(simLog);
            pushHomeTabToBackground();
            return simLogActivity;
        }

        if (place instanceof SimMsgViewer) {
            GWT.log("Launch SimMsgViewer");
            pushHomeTabToBackground();
            return clientFactory.getSimMsgViewerActivity((SimMsgViewer) place);
        }
        if (place instanceof SubmitResource) {
            GWT.log("Launch SubmitResource");
            SubmitResource submitResource = (SubmitResource) place;
            pushHomeTabToBackground();
            return clientFactory.getSubmitResourceActivity();
        }
        if (place instanceof FhirSearch) {
            GWT.log("Launch FhirSearch");
            FhirSearch fhirSearch = (FhirSearch) place;
            pushHomeTabToBackground();
            return clientFactory.getFhirSearchActivity();
        }
        if (place instanceof ResultInspector) {
            GWT.log("Launch ResultInspector");
            pushHomeTabToBackground();
            return clientFactory.getInspectorActivity((ResultInspector)place);
        }
        return null;
    }

    private void pushHomeTabToBackground() {
        Xdstools2.getHomeTab().setDisplayTab(false);
    }
}
