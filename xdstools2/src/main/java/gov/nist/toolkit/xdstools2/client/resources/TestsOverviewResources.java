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
        @Source("icons/Delete2-25.png")
        ImageResource getRemoveIcon();

        @Source("icons/Synchronize-25.png")
        ImageResource getRefreshIcon();

        @Source("icons/Play-25.png")
        ImageResource getPlayIcon();

        @Source("icons/button_round_yellow_24x24.png")
        ImageResource getBlueRoundIcon();

        @Source("icons/Checkmark-25.png")
        ImageResource getGreenCheckIcon();

        @Source("icons/round_gray_24x24.png")
        ImageResource getRoundGrayButton();

        @Source("icons/button_round_yellow_24x24.png")
        ImageResource getRoundYellowButton();

        @Source("icons/Error-25.png")
        ImageResource getDangerIcon();




}
