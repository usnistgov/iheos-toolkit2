package gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import gov.nist.toolkit.results.shared.Test;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.xdstools2.client.resources.TableResources;
import gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab.commandsWidget.CommandsCell;
import gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab.commandsWidget.CommandsColumn;
import gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab.statusCell.StatusColumn;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;

import java.util.List;
import java.util.logging.Logger;


/**
 * Created by Diane Azais local on 10/11/2015.
 */
public class TestsOverviewWidget extends CellTable<Test> {

//    ToolkitServiceAsync service = (ToolkitServiceAsync) GWT.create(ToolkitService.class);
    Logger LOGGER = Logger.getLogger("TestsOverviewWidget");

    TextColumn<Test> testnumberColumn, descriptionColumn, timeColumn;
    StatusColumn<Test> statusColumn;
    CommandsColumn<Test> buttonsColumn;
    TestsWidgetDataModel dataModel;
    Updater updater;


    public TestsOverviewWidget(TestsWidgetDataModel _dataModel, Updater _updater) {
        TableResources resources = GWT.create(TableResources.class);
        this.setStyleName(resources.cellTableStyle().toString());
        dataModel = _dataModel;
        updater = _updater;


        // --------------------------------------------------------------
        // ------------------------- Create the UI ----------------------
        // --------------------------------------------------------------

        testnumberColumn = new TextColumn<Test>() {
            @Override
            public String getValue(Test object) {
                return object.getName();
            }
        };
        addColumn(testnumberColumn, "Test Instance");

        descriptionColumn = new TextColumn<Test>() {
            @Override
            public String getValue(Test object) {
                return object.getDescription();
            }
        };
        addColumn(descriptionColumn, "Description");

        // Create custom TestButtonsCells
        buttonsColumn = new CommandsColumn<Test>() {
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

                if (value == CommandsCell.PLAY_ICON_NAME) {
                    runSingleTest(object.getId(), index);
                } else if (value == CommandsCell.REMOVE_ICON_NAME) {
                    deleteSingleTestResults(object.getId());
                } else if (value == CommandsCell.TEST_PLAN_BUTTON_NAME) {
                    //TODO retrieve test plan page based on Test or TestNumber and open link to that page
                    Window.open("link_to_HTML_page", "_blank", "");
                } else if (value == CommandsCell.LOG_BUTTON_NAME) {
                    //TODO add link to log page
                    Window.open("link_to_HTML_page", "_blank", "");
                } else if (value == CommandsCell.TEST_DESCRIPTION_BUTTON_NAME) {
                    //TODO add link to description page
                    Window.open("link_to_HTML_page", "_blank", "");
                }
            }
        });
        addColumn(buttonsColumn, "Commands");

        timeColumn = new TextColumn<Test>() {
            @Override
            public String getValue(Test object) {
                return object.getTimestamp();
            }
        };
        addColumn(timeColumn, "Time");

        statusColumn = new StatusColumn<Test>() {
            @Override
            public String getValue(Test object) {
                return object.getStatus();
            }
        };
        addColumn(statusColumn, "Status");


        // --------------------------------------------------------------
        // ----- Retrieve test results and set data into the widget -----
        // --------------------------------------------------------------

        ReloadAllTestResultsCallback testsListCallback = new ReloadAllTestResultsCallback(updater);
        loadTestsData(testsListCallback);


        // --------------------------------------------------------------
        // --------------- Set defaults UI parameters -------------------
        // --------------------------------------------------------------
        setDefaults();
    }

    /**
     * Load the full list of tests for a given Site and the current Session, as well as their parameters from the server
     * @param testsListCallback
     */
    private void loadTestsData(AsyncCallback<List<Test>> testsListCallback) {
        try {
            ClientUtils.INSTANCE.getToolkitServices().reloadAllTestResults(updater.getCurrentTestSession(), testsListCallback);
        } catch (Exception e) {
            LOGGER.warning("Failed to retrieve test results.");
        }
    }


    // --------------------------------------------------------------
    // ------- Run a single test, retrieve and display results ------
    // --------------------------------------------------------------

    AsyncCallback<Test> runSingleTestCallback = new AsyncCallback<Test>()
    {
        @Override
        public void onFailure(Throwable caught)
        { LOGGER.severe("Failed to run a test for current site and session, in the Tests Overview tab.");
        }

        @Override
        public void onSuccess(Test result)
        { dataModel.updateSingleTestResult(result);
            refreshUIData();
        }
    };

    //TODO replace the hardcoded site name with the one retrieved from the UI
    private void runSingleTest(int testId, int index){
        ClientUtils.INSTANCE.getToolkitServices()
                .runSingleTest(new Site("testEHR"), testId, runSingleTestCallback);
    }


    // --------------------------------------------------------------
    // ------ Delete logs for a single test and update display ------
    // --------------------------------------------------------------

    AsyncCallback<Test> deleteSingleLogCallback = new AsyncCallback<Test>()
    {
        @Override
        public void onFailure(Throwable caught)
        { LOGGER.severe("Failed to delete a test log, in the Tests Overview tab.");
        }

        @Override
        public void onSuccess(Test result)
        { dataModel.updateSingleTestResult(result);
            refreshUIData();
        }
    };

    //TODO replace the hardcoded site name with the one retrieved from the UI
    private void deleteSingleTestResults(int testId){
//        service.deleteSingleTestResult(testId, deleteSingleLogCallback);
    }

    /**
     * Refreshes the data view (UI)
     */
    public void refreshUIData(){
        setRowData(0, dataModel.getData());
    }

    public TestsWidgetDataModel getDataModel() {
        return dataModel;
    }

    /**
     * Default options for the cell table
     */
    private void setDefaults() {
        setWidth("100%", true);
        // Set the width of each column.
        setColumnWidth(testnumberColumn, 20, com.google.gwt.dom.client.Style.Unit.PX);
        setColumnWidth(descriptionColumn, 80, com.google.gwt.dom.client.Style.Unit.PX);
        setColumnWidth(buttonsColumn, 60, com.google.gwt.dom.client.Style.Unit.PX);
        setColumnWidth(timeColumn, 27, com.google.gwt.dom.client.Style.Unit.PX);
        setColumnWidth(statusColumn, 13, com.google.gwt.dom.client.Style.Unit.PX);

        setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
        setDisplayStyle();
    }

    /**
     * Adjusts the display styles BY ROW on the table, depending on the status of the current test.
     * If there is a need for cell or column styles one day, see commit #30b3bc81e441.
     */
    private void setDisplayStyle() {
        setRowStyles(new RowStyles<Test>() {

            @Override
            public String getStyleNames(Test rowObject, int rowIndex) {

                if (rowObject.getStatus().equals("run with warnings")){
                    return Constants.RowColor.getTestRowCss(Constants.RowColor.YELLOW);
                }
                else if (rowObject.getStatus().equals("pass")){
                    return Constants.RowColor.getTestRowCss(Constants.RowColor.GREEN);
                }
                else if (rowObject.getStatus().equals("failed")){
                    return Constants.RowColor.getTestRowCss(Constants.RowColor.RED);
                }
                else {
                    return Constants.RowColor.getTestRowCss(Constants.RowColor.WHITE);
                }
            }
        });
    }

}
