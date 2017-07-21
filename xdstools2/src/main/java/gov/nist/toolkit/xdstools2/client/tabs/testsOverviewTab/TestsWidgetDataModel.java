package gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab;

import gov.nist.toolkit.results.shared.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Diane Azais local on 10/19/2015.
 */
public class TestsWidgetDataModel {

    Logger LOGGER = Logger.getLogger("TestsWidgetDataModel");
    private List<Test> data;


    public TestsWidgetDataModel(){
        data = new ArrayList<Test>();
    }


    /**
     * Replaces a single test result in the data array
     * @param result
     */
    public void updateSingleTestResult(Test result){
        int index = findTestIndex(result);
        if (index == -1){
            LOGGER.warning("Failed to find test results returned from the server inside the test data table on the client side.");
        }
        else {
            data.set(index, result);
        }
    }

    private int findTestIndex(Test result){
        String testNumberToReplace = result.getName();
        String currentTestNumber = "";
        int index = 0;

        Iterator it = data.iterator();
        while(it.hasNext()){
            Test t = (Test) it.next();
            currentTestNumber = t.getName();
            if (currentTestNumber.equals(testNumberToReplace)){
               return index;
            }
            index++;
        }
        return -1;
    }

    public void setData(ArrayList<Test> dataArray){
        data = dataArray;
    }

    public List<Test> getData() {
        return data;
    }

}
