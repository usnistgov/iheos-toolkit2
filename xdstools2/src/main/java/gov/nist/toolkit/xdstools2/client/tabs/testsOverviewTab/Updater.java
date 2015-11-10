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

    public Updater() {
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

}
