package gov.nist.toolkit.desktop.client;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import gov.nist.toolkit.desktop.client.abstracts.AbstractToolkitActivity;
import gov.nist.toolkit.desktop.client.injection.ToolkitGinInjector;
import gov.nist.toolkit.desktop.client.tools.getDocuments.GetDocuments;
import gov.nist.toolkit.desktop.client.tools.getDocuments.GetDocumentsActivity;
import gov.nist.toolkit.desktop.client.tools.toy.Toy;
import gov.nist.toolkit.desktop.client.tools.toy.ToyActivity;

/**
 * Finds the activity to run for a given Place, used to configure an ActivityManager.
 * It binds the Places with the right Activities.
 *
 */
public class ToolkitActivityMapper implements ActivityMapper {
    private static final ToolkitGinInjector INJECTOR = ToolkitGinInjector.INSTANCE;

    public ToolkitActivityMapper() {
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
        AbstractToolkitActivity activity = null;  // the default

        if (place instanceof Toy) {
            activity = INJECTOR.getToyActivity();
            ToyActivity toyActivity = (ToyActivity) activity;
            Toy toy = (Toy) place;
            toyActivity.setName(toy.getName());
        } else if (place instanceof GetDocuments) {
            GetDocumentsActivity getDocumentsActivity = INJECTOR.getGetDocumentsActivity();
            activity = getDocumentsActivity;
            GetDocuments getDocuments = (GetDocuments) place;
            getDocumentsActivity.setName(getDocuments.getName());
        }

        if (activity != null) return activity;

        return INJECTOR.getWelcomeActivity();
    }
}
