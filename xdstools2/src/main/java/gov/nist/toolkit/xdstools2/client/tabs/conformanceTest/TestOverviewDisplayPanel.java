package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import gov.nist.toolkit.xdstools2.client.HorizontalFlowPanel;

/**
 *
 */
public class TestOverviewDisplayPanel extends HorizontalFlowPanel implements TestStatusDisplay {
    private void resetBackground() {
        removeStyleName("testOverviewHeaderSuccess");
        removeStyleName("testOverviewHeaderFail");
        removeStyleName("testOverviewHeaderNotRun");
    }

    public void labelSuccess() {
        resetBackground();
        addStyleName("testOverviewHeaderSuccess");
    }

    public void labelFailure() {
        resetBackground();
        addStyleName("testOverviewHeaderFail");
    }

    public void labelNotRun() {
        resetBackground();
        addStyleName("testOverviewHeaderNotRun");
    }

}
