package gov.nist.toolkit.xdstools2.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Created by Diane Azais local on 10/13/2015.
 */
public interface TestsOverviewResources extends ClientBundle {

        public static final TestsOverviewResources INSTANCE = GWT.create(TestsOverviewResources.class);

        // ----- Load icons -----//
        @Source("icons/delete_24x24.png")
        ImageResource getRemoveIcon();

        @Source("icons/refresh_24x24.png")
        ImageResource getRefreshIcon();

        @Source("icons/play_24x24.png")
        ImageResource getPlayIcon();

        @Source("icons/blue_button_24x24.png")
        ImageResource getBlueRoundIcon();

}
