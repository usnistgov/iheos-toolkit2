package gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab;

/**
 * Created by Diane Azais local on 10/11/2015.
 */
public class Test {
    String number;
    String description;
    TestButtonsWidget commands;
    String time;
    String status;


    public Test(String _number, String _description, String _time, String _status){
        number = _number;
        description = _description;
        commands = new TestButtonsWidget();
        time = _time;
        status =_status;
    }
}
