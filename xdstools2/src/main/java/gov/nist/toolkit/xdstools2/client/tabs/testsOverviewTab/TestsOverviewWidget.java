package gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Diane Azais local on 10/11/2015.
 */
public class TestsOverviewWidget extends CellTable<Test> {

    TextColumn<Test> testnumberColumn, descriptionColumn, timeColumn, statusColumn;
    TestButtonsColumn<Test> buttonsColumn;

    /**
     * The list of data to display.
     */
    private static final List<Test> TEST_LIST = Arrays.asList(
            new Test("10891", "test 1", " ", "04:10 PM EST", "pass"),
            new Test("17685", "test 2", " ", "04:10 PM EST", "not run")
    );

    public TestsOverviewWidget(){
        setDefaults();

        testnumberColumn = new TextColumn<Test>() {
            @Override
            public String getValue(Test object) {
                return object.number;
            }
        };
        addColumn(testnumberColumn, "Test Number");

        descriptionColumn = new TextColumn<Test>() {
            @Override
            public String getValue(Test object) {
                return object.description;
            }
        };
        addColumn(descriptionColumn, "Description");

        // Create custom TestButtonsCells
        buttonsColumn = new TestButtonsColumn<Test>() {
            @Override
            public String getValue(Test object) {
                return object.commands;
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
                return object.time;
            }
        };
        addColumn(timeColumn, "Time");

        statusColumn = new TextColumn<Test>() {
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
