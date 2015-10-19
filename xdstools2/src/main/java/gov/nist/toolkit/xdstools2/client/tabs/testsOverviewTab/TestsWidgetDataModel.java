package gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab;

import com.google.gwt.user.client.Window;
import gov.nist.toolkit.results.shared.Test;

import java.util.*;

/**
 * Created by Diane Azais local on 10/19/2015.
 */
public class TestsWidgetDataModel {

    private List<Test> data;


    public TestsWidgetDataModel(){
        data = new ArrayList<Test>();
    }


    /**
     * Replaces a single test result in the data array

     * @param result
     */
    public void updateSingleTestResult(Test result){
        String testNumberToReplace = result.getNumber();
        String currentTestNumber = "";
        int index = 0;

        Iterator it = data.iterator();
        while(it.hasNext()){
            Test t = (Test) it.next();
            currentTestNumber = t.getNumber();
            if (currentTestNumber.equals(testNumberToReplace)){
                //Window.alert(String.valueOf(index));
                data.set(index, result);
            }
            index++;
        }

    }

    public void setData(ArrayList<Test> dataArray){
        data = dataArray;
    }

    public List<Test> getData() {
        return data;
    }

}
