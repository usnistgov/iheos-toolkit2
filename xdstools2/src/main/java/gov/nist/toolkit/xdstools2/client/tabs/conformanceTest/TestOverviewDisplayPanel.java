package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import gov.nist.toolkit.xdstools2.client.widgets.HorizontalFlowPanel;

/**
 *
 */
public class TestOverviewDisplayPanel extends HorizontalFlowPanel implements TestStatusDisplay {
    private void resetBackground() {
        removeStyleName("testOverviewHeaderSuccess");
        removeStyleName("testOverviewHeaderFail");
        removeStyleName("testOverviewHeaderNotRun");
        removeStyleName("testOverviewHeaderReadme");
    }

    public void labelSuccess() {
        resetBackground();
        addStyleName("testOverviewHeaderSuccess");
    }

    public void labelReadme() {
        resetBackground();
        addStyleName("testOverviewHeaderReadme");
    }

    public void labelFailure() {
        resetBackground();
        addStyleName("testOverviewHeaderFail");
    }

    public void labelNotRun() {
        resetBackground();
        addStyleName("testOverviewHeaderNotRun");
//      addStyleName("test-row-yellow");
    }

    public void addExtraStyle(String extra) {
        addStyleName(extra);
    }

    public void removeExtraStyle(String extra) {
        removeStyleName(extra);
    }

}
