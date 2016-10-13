package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import gov.nist.toolkit.xdstools2.client.widgets.HorizontalFlowPanel;

/**
 *
 */
public class TestDisplayHeader extends HorizontalFlowPanel {
    private void resetBackground() {
        removeStyleName("testOverviewHeaderSuccess");
        removeStyleName("testOverviewHeaderFail");
        removeStyleName("testOverviewHeaderNotRun");
    }

    public void setBackgroundColorSuccess() {
        resetBackground();
        addStyleName("testOverviewHeaderSuccess");
    }

    public void setBackgroundColorFailure() {
        resetBackground();
        addStyleName("testOverviewHeaderFail");
    }

    public void setBackgroundColorNotRun() {
        resetBackground();
        addStyleName("testOverviewHeaderNotRun");
    }

}
