package gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import gov.nist.toolkit.results.shared.Test;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.xdstools2.client.ToolkitService;
import gov.nist.toolkit.xdstools2.client.ToolkitServiceAsync;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Diane Azais local on 10/11/2015.
 */
public class TestsOverviewWidget extends CellTable<Test> {

    ToolkitServiceAsync service = (ToolkitServiceAsync) GWT.create(ToolkitService.class);
    Logger LOGGER = Logger.getLogger("TestsOverviewWidget");

    TextColumn<Test> testnumberColumn, descriptionColumn, timeColumn, statusColumn;
    TestButtonsColumn<Test> buttonsColumn;



    public TestsOverviewWidget(){
        setDefaults();


        // ----- Create the UI -----

        testnumberColumn = new TextColumn<Test>() {
            @Override
            public String getValue(Test object) {
                return object.getNumber();
            }
        };
        addColumn(testnumberColumn, "Test Number");

        descriptionColumn = new TextColumn<Test>() {
            @Override
            public String getValue(Test object) {
                return object.getDescription();
            }
        };
        addColumn(descriptionColumn, "Description");

        // Create custom TestButtonsCells
        buttonsColumn = new TestButtonsColumn<Test>() {
            @Override
            public String getValue(Test object) {
                return object.getCommands();
            }
        };
        buttonsColumn.setFieldUpdater(new FieldUpdater<Test, String>() {

            @Override
            /**
             * Return the element of the buttons cell that was clicked (icon or button).
             */
            public void update(int index, Test object, String value) {
                Window.alert("This is the field updater, with value: " + value + " Index: " + index);
                //TODO add appropriate action
            }
        });
        addColumn(buttonsColumn, "Commands");

        timeColumn = new TextColumn<Test>() {
            @Override
            public String getValue(Test object) {
                return object.getTime();
            }
        };
        addColumn(timeColumn, "Time");

        statusColumn = new TextColumn<Test>() {
            @Override
            public String getValue(Test object) {
                return object.getStatus();
            }
        };
        addColumn(statusColumn, "Status");


        // ----- Push the data into the widget. -----

        AsyncCallback<List<Test>> callback = new AsyncCallback<List<Test>>()
        {
            @Override
            public void onFailure(Throwable caught)
            { LOGGER.severe("Failed to load the list of available tests for current site and session, in the Tests Overview tab.");
                 }

            @Override
            public void onSuccess(List<Test> result)
            { setRowData(0, result); }
        };
        // TODO the currently selected Site must be retrieved
        service.getTestsList(new Site("testEHR"), callback);
    }


    /**
     * Default options for the cell table
     */
    private void setDefaults(){
        setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
    }
}
