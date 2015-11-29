package gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab.commandsWidget;

import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.xdstools2.client.resources.TestsOverviewResources;
import gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab.Updater;
import gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab.Utils;


/**
 * Created by Diane Azais local on 10/15/2015.
 */
public class CommandsWidget extends HorizontalPanel {
    TestsOverviewResources RESOURCES = TestsOverviewResources.INSTANCE;
    Updater updater;


    public static String PLAY_ALL_ICON_HINT = "Run all tests";
    public static String REMOVE_ALL_ICON_HINT = "Delete all test results (cannot be undone!)";
    public static String REFRESH_ALL_ICON_HINT = "Reload test results";

    private Image PLAY_ALL_ICON = new Image(RESOURCES.getPlayIconBlack());
    private Image REMOVE_ALL_ICON = new Image(RESOURCES.getDeleteIconBlack());
    private Image REFRESH_ALL_ICON = new Image(RESOURCES.getRefreshIconBlack());

    private Button playAllButton, removeAllButton, refreshAllButton;



    public CommandsWidget(Updater _updater){

        //----- View updater -----
        updater = _updater;


        //----- Create the widget -----
        FlowPanel spacer = new FlowPanel();
        spacer.setWidth("550px");

        playAllButton = Utils.makeIconButton(PLAY_ALL_ICON_HINT, PLAY_ALL_ICON);
        removeAllButton = Utils.makeIconButton(REMOVE_ALL_ICON_HINT, REMOVE_ALL_ICON);
        refreshAllButton = Utils.makeIconButton(REFRESH_ALL_ICON_HINT, REFRESH_ALL_ICON);

        ButtonClickHandler clickHandler = new ButtonClickHandler(this);
        clickHandler.setViewUpdater(updater);
        playAllButton.addClickHandler(clickHandler);
        removeAllButton.addClickHandler(clickHandler);
        refreshAllButton.addClickHandler(clickHandler);

        setWidth("100%");
        setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);

        add(spacer);
        add(playAllButton);
        add(removeAllButton);
        add(refreshAllButton);
    }


    public Button getPlayAllButton() {
        return playAllButton;
    }

    public Button getRemoveAllButton() {
        return removeAllButton;
    }

    public Button getRefreshAllButton() {
        return refreshAllButton;
    }

}
