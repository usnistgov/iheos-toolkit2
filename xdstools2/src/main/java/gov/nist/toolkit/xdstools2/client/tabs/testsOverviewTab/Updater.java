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
    private static Updater singleton;

    protected Updater(){
        // singleton empty constructor
    }

    public static Updater getUpdater(TestsOverviewWidget _testsOverviewWidget){
        if (singleton == null){
            testsOverviewWidget = _testsOverviewWidget;
            singleton = new Updater();
        }
        return singleton;
    }


    public void updateTestView(){
        testsOverviewWidget.refreshUIData();
    }

    public void updateTestViewData(ArrayList<Test> dataArray){
        testsOverviewWidget.getDataModel().setData(dataArray);
    }
}
