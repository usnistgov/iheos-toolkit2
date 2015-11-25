package gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab.statusCell;

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Image;
import gov.nist.toolkit.results.shared.Test;
import gov.nist.toolkit.xdstools2.client.resources.TestsOverviewResources;

/**
 * Created by Diane Azais local on 10/13/2015.
 */
public abstract class StatusColumnOld<T> extends Column<T, ImageResource> {
    TestsOverviewResources RESOURCES = TestsOverviewResources.INSTANCE;

 //   private Image NOT_RUN_ICON = new Image(RESOURCES.getRoundGrayButton());
 //   private Image HAS_WARNINGS_ICON = new Image(RESOURCES.getRoundYellowButton());
//    private Image PASSED_ICON = new Image(RESOURCES.getGreenCheckIcon());
 //   private Image FAILED_ICON = new Image(RESOURCES.getDangerIcon());

    private ImageResource NOT_RUN_RESOURCE = RESOURCES.getRoundGrayButton();
    private ImageResource HAS_WARNINGS_RESOURCE = RESOURCES.getRoundYellowButton();
    private ImageResource PASSED_RESOURCE = RESOURCES.getGreenCheckIcon();
    private ImageResource FAILED_RESOURCE = RESOURCES.getDangerIcon();



    public StatusColumnOld() {
        super(new ImageResourceCell() {

            public ImageResource getValue(T object) {
                Test t = (Test)object;
                String status = t.getStatus();
                if (status == null){
                    return null; //RESOURCES.getRoundGrayButton();
                }
                return null;//resources.getImageResource();
            }
        });
    }

}

