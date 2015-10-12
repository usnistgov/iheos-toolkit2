package gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by Diane Azais local on 10/11/2015.
 */
public class TestButtonsWidget extends Widget {
    public HorizontalPanel mainPanel;

    public TestButtonsWidget(){
        mainPanel = new HorizontalPanel();

        Button testPlanButton = new Button("Test Plan");
        mainPanel.add(testPlanButton);

        Button logButton = new Button("Log");
        mainPanel.add(logButton);

        Button descriptionButton = new Button("Full Test Description");
        mainPanel.add(descriptionButton);
    }
}
