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
        @Source("icons/delete.png")
        ImageResource getRemoveIcon();

        @Source("icons/refresh-128.png")
        ImageResource getRefreshIcon();

        @Source("icons/play.png")
        ImageResource getPlayIcon();

        @Source("icons/Button_Blank_Blue_Icon_32.png")
        ImageResource getBlueRoundIcon();

}
