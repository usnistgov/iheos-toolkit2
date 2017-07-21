package gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab.commandsWidget;

import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab.Updater;
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
        HTML header = new HTML();
        String space = "&nbsp&nbsp&nbsp&nbsp&nbsp";
        header.setHTML("<h3>Run: 4/6" + space + "Failed: 1/6" + space + "Passed: 1/6 (17%)</h3>");

        FlowPanel spacer = new FlowPanel();
        spacer.setWidth("500px");

        playAllButton = ButtonFactory.createIconButton(ButtonType.PLAY_BUTTON, PLAY_ALL_ICON_HINT);
        removeAllButton = ButtonFactory.createIconButton(ButtonType.DELETE_BUTTON, REMOVE_ALL_ICON_HINT);
        refreshAllButton = ButtonFactory.createIconButton(ButtonType.REFRESH_BUTTON, REFRESH_ALL_ICON_HINT);

        ButtonClickHandler clickHandler = new ButtonClickHandler(this);
        clickHandler.setViewUpdater(updater);
        playAllButton.addClickHandler(clickHandler);
        removeAllButton.addClickHandler(clickHandler);
        refreshAllButton.addClickHandler(clickHandler);

        setDisplayProperties();

        add(header);
        add(spacer);
        add(playAllButton);
        add(refreshAllButton);
        add(removeAllButton);
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
