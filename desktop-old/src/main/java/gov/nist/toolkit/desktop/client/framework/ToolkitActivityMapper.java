package gov.nist.toolkit.desktop.client.framework;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import gov.nist.toolkit.desktop.client.toolkit.ToolkitActivity;
import gov.nist.toolkit.desktop.client.toolkit.ToolkitPlace;

import javax.inject.Inject;

/**
 *
 */
public class ToolkitActivityMapper implements ActivityMapper {
    private ClientFactory clientFactory;


    @Inject
    public ToolkitActivityMapper(ClientFactory clientFactory) {
        super();
        this.clientFactory = clientFactory;
    }

    @Override
    public Activity getActivity(Place place) {
        AbstractActivity activity = null;

        // Default place - toolkit with no tools open
        if (place instanceof ToolkitPlace) {
            return new ToolkitActivity((ToolkitPlace) place, clientFactory);
        }
        return null;
    }
}
