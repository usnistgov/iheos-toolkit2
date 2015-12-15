package gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab;

import gov.nist.toolkit.results.shared.Test;

import java.util.ArrayList;

/**
 * Created by Diane Azais local on 10/21/2015.
 *
 * Singleton class that implements a data and view updater for the widgets
 */
public class Updater {
    private static TestsOverviewWidget testsOverviewWidget;
    private TestsOverviewTab testsTab;

    public Updater(TestsOverviewTab _testsTab) {
        testsTab = _testsTab;
    }

    public void updateTestView() {
        testsOverviewWidget.refreshUIData();
    }

    public void updateTestData(ArrayList<Test> dataArray) {
        testsOverviewWidget.getDataModel().setData(dataArray);
    }

    public void setTestsOverviewWidget(TestsOverviewWidget _testsOverviewWidget) {
        testsOverviewWidget = _testsOverviewWidget;
    }

    /**
     * Accesses the Session object from inside the TabbedWindow contained of the Tab
     * @return the current user session name
     */
    public String getCurrentTestSession(){
        return testsTab.getCurrentTestSession();
    }

}
