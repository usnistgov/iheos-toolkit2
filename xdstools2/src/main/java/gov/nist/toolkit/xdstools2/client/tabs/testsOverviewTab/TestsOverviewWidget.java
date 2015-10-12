package gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Diane Azais local on 10/11/2015.
 */
public class TestsOverviewWidget extends CellTable<Test> {

    /**
     * The list of data to display.
     */
    private static final List<Test> TEST_LIST = Arrays.asList(
            new Test("10891", "test 1", " ", "04:10 PM EST", "pass"),
            new Test("17685", "test 2", " ", "04:10 PM EST", "not run")
    );

    public TestsOverviewWidget(){
        setDefaults();

        TextColumn<Test> testnumberColumn = new TextColumn<Test>() {
            @Override
            public String getValue(Test object) {
                return object.number;
            }
        };
        addColumn(testnumberColumn, "Test Number");

        TextColumn<Test> descriptionColumn = new TextColumn<Test>() {
            @Override
            public String getValue(Test object) {
                return object.description;
            }
        };
        addColumn(descriptionColumn, "Description");

        // Create custom TestButtonsCells
        TestButtonsColumn<Test> buttonsColumn = new TestButtonsColumn<Test>() {
            @Override
            public String getValue(Test object) {
                return object.commands;
            }
        };
        addColumn(buttonsColumn, "Commands");

        TextColumn<Test> timeColumn = new TextColumn<Test>() {
            @Override
            public String getValue(Test object) {
                return object.time;
            }
        };
        addColumn(timeColumn, "Time");

        TextColumn<Test> statusColumn = new TextColumn<Test>() {
            @Override
            public String getValue(Test object) {
                return object.status;
            }
        };
        addColumn(statusColumn, "Status");


        // Push the data into the widget.
        setRowData(0, TEST_LIST);
    }


    private void setDefaults(){
        setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
    }
}
