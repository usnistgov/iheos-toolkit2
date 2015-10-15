package gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.xdstools2.client.resources.TestsOverviewResources;



/**
 * Created by Diane Azais local on 10/15/2015.
 */
public class CommandsWidget extends HorizontalPanel {
    TestsOverviewResources RESOURCES = TestsOverviewResources.INSTANCE;


    public static String PLAY_ALL_ICON_NAME = "PLAY_ALL_ICON";
    public static String REMOVE_ALL_ICON_NAME = "REMOVE_ALL_ICON";
    public static String REFRESH_ALL_ICON_NAME = "REFRESH_ALL_ICON";

    private Image PLAY_ALL_ICON = new Image(RESOURCES.getPlayIcon());
    private Image REMOVE_ALL_ICON = new Image(RESOURCES.getRemoveIcon());
    private Image REFRESH_ALL_ICON = new Image(RESOURCES.getRefreshIcon());

    private Button playAllButton, removeAllButton, refreshAllButton;


    public CommandsWidget(){
        FlowPanel spacer = new FlowPanel();
        spacer.setWidth("550px");

        playAllButton = new Button();
        removeAllButton = new Button();
        refreshAllButton = new Button();

        playAllButton.getElement().appendChild(PLAY_ALL_ICON.getElement());
        removeAllButton.getElement().appendChild(REMOVE_ALL_ICON.getElement());
        refreshAllButton.getElement().appendChild(REFRESH_ALL_ICON.getElement());

        setWidth("100%");
        setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

        add(spacer);
        add(playAllButton);
        add(removeAllButton);
        add(refreshAllButton);
    }
}
