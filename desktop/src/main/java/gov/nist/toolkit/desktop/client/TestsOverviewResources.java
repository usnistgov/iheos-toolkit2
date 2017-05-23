package gov.nist.toolkit.desktop.client;

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
        ImageResource getDeleteIconWhite24();

        @Source("icons/ic_delete_black_24dp_1x.png")
        ImageResource getDeleteIconBlack24();

        @Source("icons/ic_delete_white_36dp_1x.png")
        ImageResource getDeleteIconWhite36();

        @Source("icons/ic_delete_black_36dp_1x.png")
        ImageResource getDeleteIconBlack36();

        @Source("icons/ic_refresh_white_24dp_1x.png")
        ImageResource getRefreshIconWhite24();

        @Source("icons/ic_refresh_white_36dp_1x.png")
        ImageResource getRefreshIconWhite36();

        @Source("icons/ic_refresh_black_24dp_1x.png")
        ImageResource getRefreshIconBlack24();

        @Source("icons/ic_refresh_black_36dp_1x.png")
        ImageResource getRefreshIconBlack36();

        @Source("icons/ic_play_arrow_white_24dp_1x.png")
        ImageResource getPlayIconWhite24();

        @Source("icons/ic_play_arrow_white_36dp_1x.png")
        ImageResource getPlayIconWhite36();

        @Source("icons/ic_play_arrow_black_24dp_1x.png")
        ImageResource getPlayIconBlack24();

        @Source("icons/ic_play_arrow_black_36dp_1x.png")
        ImageResource getPlayIconBlack36();

        @Source("icons/ic_done_white_24dp_1x.png")
        ImageResource getCheckIconWhite24();

        @Source("icons/ic_done_black_24dp_1x.png")
        ImageResource getCheckIconBlack24();

        @Source("icons/ic_circle_outline_white_24dp_1x.png")
        ImageResource getCircleOutlineIconWhite24();

        @Source("icons/ic_remove_white_24dp_1x.png")
        ImageResource getRemoveIconWhite24();

        @Source("icons/ic_info_outline_white_24dp_1x.png")
        ImageResource getInfoIconWhite24();

        @Source("icons/ic_warning_white_24dp_1x.png")
        ImageResource getDangerIconWhite24();



}