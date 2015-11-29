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
        @Source("icons/ic_delete_white_24dp_1x.png")
        ImageResource getDeleteIconWhite();

        @Source("icons/ic_delete_black_24dp_1x.png")
        ImageResource getDeleteIconBlack();

        @Source("icons/ic_refresh_white_24dp_1x.png")
        ImageResource getRefreshIconWhite();

        @Source("icons/ic_refresh_black_24dp_1x.png")
        ImageResource getRefreshIconBlack();

        @Source("icons/ic_play_arrow_white_24dp_1x.png")
        ImageResource getPlayIconWhite();

        @Source("icons/ic_play_arrow_black_24dp_1x.png")
        ImageResource getPlayIconBlack();

        @Source("icons/ic_done_white_24dp_1x.png")
        ImageResource getCheckIconWhite();

        @Source("icons/ic_done_black_24dp_1x.png")
        ImageResource getCheckIconBlack();

        @Source("icons/ic_circle_outline_white_24dp_1x.png")
        ImageResource getCircleOutlineIconWhite();

        @Source("icons/ic_info_outline_white_24dp_1x.png")
        ImageResource getInfoIconWhite();

        @Source("icons/ic_warning_white_24dp_1x.png")
        ImageResource getDangerIconWhite();


}
