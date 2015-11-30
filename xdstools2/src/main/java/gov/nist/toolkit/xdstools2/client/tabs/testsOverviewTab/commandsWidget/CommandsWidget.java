package gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab.commandsWidget;

import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.xdstools2.client.resources.TestsOverviewResources;
import gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab.Updater;
import gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab.Utils;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.ButtonFactory;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.ButtonType;


/**
 * Created by Diane Azais local on 10/15/2015.
 */
public class CommandsWidget extends HorizontalPanel {
    Updater updater;


    public static String PLAY_ALL_ICON_HINT = "Run all tests";
    public static String REMOVE_ALL_ICON_HINT = "Delete all test results (cannot be undone!)";
    public static String REFRESH_ALL_ICON_HINT = "Reload test results";


    private Button playAllButton, removeAllButton, refreshAllButton;


    public CommandsWidget(Updater _updater){

        //----- View updater -----
        updater = _updater;


        //----- Create the widget -----
        FlowPanel spacer = new FlowPanel();
        spacer.setWidth("550px");

        playAllButton = ButtonFactory.createIconButton(ButtonType.PLAY_BUTTON, PLAY_ALL_ICON_HINT);
        removeAllButton = ButtonFactory.createIconButton(ButtonType.DELETE_BUTTON, REMOVE_ALL_ICON_HINT);
        refreshAllButton = ButtonFactory.createIconButton(ButtonType.REFRESH_BUTTON, REFRESH_ALL_ICON_HINT);

        ButtonClickHandler clickHandler = new ButtonClickHandler(this);
        clickHandler.setViewUpdater(updater);
        playAllButton.addClickHandler(clickHandler);
        removeAllButton.addClickHandler(clickHandler);
        refreshAllButton.addClickHandler(clickHandler);

        setDisplayProperties();

        add(spacer);
        add(playAllButton);
        add(removeAllButton);
        add(refreshAllButton);
    }


    public void setDisplayProperties() {
        setWidth("100%");
        setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        setStyleName("testOverviewHeader");
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
